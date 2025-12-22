package nl.overheid.ecm.mtom.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception for validation errors (missing required fields, invalid formats, business rule violations)
 */
public class ValidationException extends RuntimeException {
    private final ErrorDetails errorDetails;
    private final List<String> validationErrors;

    public ValidationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorDetails = ErrorDetails.of(errorCode, message);
        this.validationErrors = new ArrayList<>();
    }

    public ValidationException(ErrorCode errorCode, String message, List<String> validationErrors) {
        super(message);
        this.errorDetails = ErrorDetails.of(errorCode, message);
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
        if (!this.validationErrors.isEmpty()) {
            this.errorDetails.withDetail("validationErrors", this.validationErrors);
        }
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
