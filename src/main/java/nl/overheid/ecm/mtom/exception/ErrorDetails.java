package nl.overheid.ecm.mtom.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Structured error details for all exceptions
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {
    private String errorCode;
    private String errorMessage;
    private Map<String, Object> errorDetails;
    private Instant timestamp;
    private String messageId;
    private String clientId;
    private Boolean retryable;

    public ErrorDetails() {
        this.timestamp = Instant.now();
        this.errorDetails = new HashMap<>();
    }

    public static ErrorDetails of(ErrorCode errorCode, String message) {
        ErrorDetails details = new ErrorDetails();
        details.setErrorCode(errorCode.getCode());
        details.setErrorMessage(message != null ? message : errorCode.getDescription());
        details.setRetryable(errorCode.isRetryable());
        return details;
    }

    public ErrorDetails withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public ErrorDetails withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ErrorDetails withDetail(String key, Object value) {
        this.errorDetails.put(key, value);
        return this;
    }

    // Getters and Setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(Map<String, Object> errorDetails) {
        this.errorDetails = errorDetails;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }
}
