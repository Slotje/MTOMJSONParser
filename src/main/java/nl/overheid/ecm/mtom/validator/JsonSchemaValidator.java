package nl.overheid.ecm.mtom.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import nl.overheid.ecm.mtom.exception.ErrorCode;
import nl.overheid.ecm.mtom.exception.ValidationException;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator for JSON Schema validation
 */
@ApplicationScoped
public class JsonSchemaValidator {
    private static final Logger LOG = Logger.getLogger(JsonSchemaValidator.class);

    @Inject
    ObjectMapper objectMapper;

    private JsonSchema clientConfigSchema;
    private JsonSchema parsedMessageSchema;

    @PostConstruct
    public void init() {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

            // Load client configuration schema
            InputStream clientConfigStream = getClass().getResourceAsStream(
                "/schemas/client-configuration-schema.json");
            if (clientConfigStream != null) {
                clientConfigSchema = factory.getSchema(clientConfigStream);
                LOG.info("Loaded client configuration schema");
            } else {
                LOG.warn("Client configuration schema not found");
            }

            // Load parsed message schema
            InputStream parsedMessageStream = getClass().getResourceAsStream(
                "/schemas/parsed-message-schema.json");
            if (parsedMessageStream != null) {
                parsedMessageSchema = factory.getSchema(parsedMessageStream);
                LOG.info("Loaded parsed message schema");
            } else {
                LOG.warn("Parsed message schema not found");
            }

        } catch (Exception e) {
            LOG.error("Failed to initialize JSON schemas", e);
            throw new RuntimeException("Failed to initialize JSON schemas", e);
        }
    }

    /**
     * Validate client configuration against schema
     */
    public void validateClientConfiguration(Object config) {
        if (clientConfigSchema == null) {
            LOG.warn("Client configuration schema not available, skipping validation");
            return;
        }

        try {
            JsonNode jsonNode = objectMapper.valueToTree(config);
            Set<ValidationMessage> errors = clientConfigSchema.validate(jsonNode);

            if (!errors.isEmpty()) {
                List<String> errorMessages = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toList());

                LOG.errorf("Client configuration validation failed: %s", errorMessages);
                throw new ValidationException(
                    ErrorCode.VALID_004,
                    "Client configuration validation failed",
                    errorMessages
                );
            }

            LOG.debug("Client configuration validation successful");

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error during client configuration validation", e);
            throw new ValidationException(
                ErrorCode.VALID_004,
                "Failed to validate client configuration: " + e.getMessage()
            );
        }
    }

    /**
     * Validate parsed message against schema
     */
    public void validateParsedMessage(Object message) {
        if (parsedMessageSchema == null) {
            LOG.warn("Parsed message schema not available, skipping validation");
            return;
        }

        try {
            JsonNode jsonNode = objectMapper.valueToTree(message);
            Set<ValidationMessage> errors = parsedMessageSchema.validate(jsonNode);

            if (!errors.isEmpty()) {
                List<String> errorMessages = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toList());

                LOG.errorf("Parsed message validation failed: %s", errorMessages);
                throw new ValidationException(
                    ErrorCode.VALID_004,
                    "Parsed message validation failed",
                    errorMessages
                );
            }

            LOG.debug("Parsed message validation successful");

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error during parsed message validation", e);
            throw new ValidationException(
                ErrorCode.VALID_004,
                "Failed to validate parsed message: " + e.getMessage()
            );
        }
    }
}
