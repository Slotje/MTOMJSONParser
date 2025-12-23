package nl.overheid.ecm.mtom.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import nl.overheid.ecm.mtom.model.ClientConfiguration;
import nl.overheid.ecm.mtom.model.ParsedMessage;
import nl.overheid.ecm.mtom.parser.MTOMParser;
import nl.overheid.ecm.mtom.parser.AutomaticMTOMParser;
import nl.overheid.ecm.mtom.validator.JsonSchemaValidator;
import nl.overheid.ecm.mtom.validator.MessageValidator;
import org.jboss.logging.Logger;

/**
 * Main service for MTOM to JSON conversion
 */
@ApplicationScoped
public class MTOMConversionService {
    private static final Logger LOG = Logger.getLogger(MTOMConversionService.class);

    @Inject
    MTOMParser mtomParser;

    @Inject
    AutomaticMTOMParser automaticMTOMParser;

    @Inject
    ConfigurationService configurationService;

    @Inject
    MessageValidator messageValidator;

    @Inject
    JsonSchemaValidator jsonSchemaValidator;

    /**
     * Convert MTOM message to JSON (full ParsedMessage object)
     *
     * @param mtomXml MTOM message as XML string
     * @param clientId Client identifier
     * @return ParsedMessage with metadata and content
     */
    public ParsedMessage convertToJson(String mtomXml, String clientId) {
        LOG.infof("Starting MTOM to JSON conversion for client: %s", clientId);

        // Phase 1 & 2: Configuration retrieval
        ClientConfiguration config = configurationService.getConfiguration(clientId);

        // Check if processing is enabled
        if (config.getProcessingRules() != null &&
            Boolean.FALSE.equals(config.getProcessingRules().getProcessingEnabled())) {
            LOG.warnf("Processing is disabled for client: %s", clientId);
            throw new IllegalStateException(
                String.format("Processing is disabled for client: %s", clientId));
        }

        // Phase 3 & 4: Parsing and JSON construction
        ParsedMessage parsedMessage = mtomParser.parse(mtomXml, config);

        // Phase 5: Validation
        // Business rule validation
        messageValidator.validate(parsedMessage, config);

        // JSON schema validation (commented out for simple mapping)
        // jsonSchemaValidator.validateParsedMessage(parsedMessage);

        LOG.infof("Successfully converted MTOM message %s to JSON", parsedMessage.getMessageId());

        return parsedMessage;
    }

    /**
     * Convert MTOM message to simple JSON (only mapped metadata fields)
     *
     * @param mtomXml MTOM message as XML string
     * @param clientId Client identifier
     * @return Map with only the mapped metadata fields
     */
    public java.util.Map<String, Object> convertToSimpleJson(String mtomXml, String clientId) {
        LOG.infof("Starting MTOM to simple JSON conversion for client: %s", clientId);

        ParsedMessage parsedMessage = convertToJson(mtomXml, clientId);

        // Return only the metadata (the mapped fields)
        return parsedMessage.getMetadata();
    }

    /**
     * Validate MTOM message without full conversion
     */
    public void validateMessage(String mtomXml, String clientId) {
        LOG.infof("Validating MTOM message for client: %s", clientId);

        ClientConfiguration config = configurationService.getConfiguration(clientId);
        ParsedMessage parsedMessage = mtomParser.parse(mtomXml, config);
        messageValidator.validate(parsedMessage, config);

        LOG.infof("MTOM message validation successful for client: %s", clientId);
    }

    /**
     * Automatically parse MTOM XML without client configuration.
     * This is a proof of concept that extracts all fields from the MTOM structure.
     *
     * @param mtomXml MTOM message as XML string
     * @return Map with all automatically extracted fields
     */
    public java.util.Map<String, Object> parseAutomatically(String mtomXml) {
        LOG.info("Starting automatic MTOM parsing (configuration-free)");

        java.util.Map<String, Object> result = automaticMTOMParser.parse(mtomXml);

        LOG.infof("Successfully parsed MTOM message, extracted %d fields", result.size());
        return result;
    }
}
