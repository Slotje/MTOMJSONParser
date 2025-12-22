package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Business information for client configuration
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessInfo {
    @NotBlank(message = "contactEmail is required")
    @Email(message = "contactEmail must be a valid email address")
    private String contactEmail;

    @NotBlank(message = "supportGroup is required")
    private String supportGroup;

    @Min(value = 1, message = "maxMessageSize must be at least 1")
    private Integer maxMessageSize;

    @Min(value = 1, message = "maxMessagesPerDay must be at least 1")
    private Integer maxMessagesPerDay;

    public BusinessInfo() {
    }

    public BusinessInfo(String contactEmail, String supportGroup) {
        this.contactEmail = contactEmail;
        this.supportGroup = supportGroup;
    }

    // Getters and Setters
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSupportGroup() {
        return supportGroup;
    }

    public void setSupportGroup(String supportGroup) {
        this.supportGroup = supportGroup;
    }

    public Integer getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(Integer maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public Integer getMaxMessagesPerDay() {
        return maxMessagesPerDay;
    }

    public void setMaxMessagesPerDay(Integer maxMessagesPerDay) {
        this.maxMessagesPerDay = maxMessagesPerDay;
    }
}
