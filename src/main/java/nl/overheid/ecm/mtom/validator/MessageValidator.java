package nl.overheid.ecm.mtom.validator;

import jakarta.enterprise.context.ApplicationScoped;
import nl.overheid.ecm.mtom.exception.ErrorCode;
import nl.overheid.ecm.mtom.exception.ValidationException;
import nl.overheid.ecm.mtom.model.ClientConfiguration;
import nl.overheid.ecm.mtom.model.ParsedMessage;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for business rules
 */
@ApplicationScoped
public class MessageValidator {
    private static final Logger LOG = Logger.getLogger(MessageValidator.class);

    /**
     * Validate parsed message against business rules
     */
    public void validate(ParsedMessage message, ClientConfiguration config) {
        List<String> errors = new ArrayList<>();

        // Validate message size if configured
        if (config.getBusinessInfo().getMaxMessageSize() != null &&
            message.getContent() != null &&
            message.getContent().getSize() != null) {

            long maxSize = config.getBusinessInfo().getMaxMessageSize();
            long actualSize = message.getContent().getSize();

            if (actualSize > maxSize) {
                String error = String.format(
                    "Message size (%d bytes) exceeds maximum allowed size (%d bytes)",
                    actualSize, maxSize
                );
                errors.add(error);
                LOG.warnf("Message %s: %s", message.getMessageId(), error);
            }
        }

        // Validate required metadata fields
        validateRequiredMetadata(message, config, errors);

        // If there are validation errors, throw exception
        if (!errors.isEmpty()) {
            throw new ValidationException(
                ErrorCode.VALID_003,
                "Business rule validation failed",
                errors
            );
        }

        LOG.debugf("Message %s passed business rule validation", message.getMessageId());
    }

    private void validateRequiredMetadata(ParsedMessage message,
                                         ClientConfiguration config,
                                         List<String> errors) {
        config.getMetadataMapping().getFields().stream()
            .filter(field -> Boolean.TRUE.equals(field.getRequired()))
            .forEach(field -> {
                String targetProperty = field.getTargetProperty();
                Object value = message.getMetadata().get(targetProperty);

                if (value == null) {
                    String error = String.format(
                        "Required metadata field missing: %s (source: %s)",
                        targetProperty, field.getSourceField()
                    );
                    errors.add(error);
                    LOG.warnf("Message %s: %s", message.getMessageId(), error);
                }
            });
    }
}
