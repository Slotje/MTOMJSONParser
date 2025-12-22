package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;

/**
 * Processing rules for client configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingRules {
    @Min(value = 1, message = "retentionDays must be at least 1")
    private Integer retentionDays = 30;

    private Boolean autoRetryEnabled = true;
    private Boolean processingEnabled = true;

    public ProcessingRules() {
    }

    // Getters and Setters
    public Integer getRetentionDays() {
        return retentionDays;
    }

    public void setRetentionDays(Integer retentionDays) {
        this.retentionDays = retentionDays;
    }

    public Boolean getAutoRetryEnabled() {
        return autoRetryEnabled;
    }

    public void setAutoRetryEnabled(Boolean autoRetryEnabled) {
        this.autoRetryEnabled = autoRetryEnabled;
    }

    public Boolean getProcessingEnabled() {
        return processingEnabled;
    }

    public void setProcessingEnabled(Boolean processingEnabled) {
        this.processingEnabled = processingEnabled;
    }
}
