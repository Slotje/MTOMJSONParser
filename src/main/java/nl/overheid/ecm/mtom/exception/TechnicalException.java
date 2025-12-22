package nl.overheid.ecm.mtom.exception;

/**
 * Exception for technical errors (timeouts, memory issues, connection problems)
 */
public class TechnicalException extends RuntimeException {
    private final ErrorDetails errorDetails;

    public TechnicalException(ErrorCode errorCode, String message) {
        super(message);
        this.errorDetails = ErrorDetails.of(errorCode, message);
    }

    public TechnicalException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorDetails = ErrorDetails.of(errorCode, message);
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }
}
