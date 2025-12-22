package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Complete client configuration for MTOM to JSON conversion
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientConfiguration {
    @NotBlank(message = "clientId is required")
    private String clientId;

    @NotBlank(message = "clientName is required")
    private String clientName;

    @NotNull(message = "metadataMapping is required")
    @Valid
    private MetadataMapping metadataMapping;

    @NotNull(message = "businessInfo is required")
    @Valid
    private BusinessInfo businessInfo;

    @Valid
    private ProcessingRules processingRules;

    public ClientConfiguration() {
    }

    public ClientConfiguration(String clientId, String clientName) {
        this.clientId = clientId;
        this.clientName = clientName;
    }

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public MetadataMapping getMetadataMapping() {
        return metadataMapping;
    }

    public void setMetadataMapping(MetadataMapping metadataMapping) {
        this.metadataMapping = metadataMapping;
    }

    public BusinessInfo getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(BusinessInfo businessInfo) {
        this.businessInfo = businessInfo;
    }

    public ProcessingRules getProcessingRules() {
        return processingRules;
    }

    public void setProcessingRules(ProcessingRules processingRules) {
        this.processingRules = processingRules;
    }
}
