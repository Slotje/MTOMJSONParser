package nl.overheid.ecm.mtom.exception;

/**
 * Exception for MTOM parsing errors (structural issues with the MTOM message)
 */
public class MTOMParsingException extends RuntimeException {
    private final ErrorDetails errorDetails;

    public MTOMParsingException(ErrorCode errorCode, String message) {
        super(message);
        this.errorDetails = ErrorDetails.of(errorCode, message);
    }

    public MTOMParsingException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorDetails = ErrorDetails.of(errorCode, message);
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }
}
