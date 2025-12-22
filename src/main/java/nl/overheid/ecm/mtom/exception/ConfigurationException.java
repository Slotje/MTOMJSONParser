package nl.overheid.ecm.mtom.exception;

/**
 * Exception for client configuration errors
 */
public class ConfigurationException extends RuntimeException {
    private final ErrorDetails errorDetails;

    public ConfigurationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorDetails = ErrorDetails.of(errorCode, message);
    }

    public ConfigurationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorDetails = ErrorDetails.of(errorCode, message);
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }
}
