package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a mapping from a source field in MTOM to a target property in FileNet
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldMapping {
    @NotBlank(message = "sourceField is required")
    private String sourceField;

    @NotBlank(message = "targetProperty is required")
    private String targetProperty;

    @NotNull(message = "required flag is required")
    private Boolean required;

    private String format;
    private String defaultValue;
    private Boolean multiValue = false;

    public FieldMapping() {
    }

    public FieldMapping(String sourceField, String targetProperty, Boolean required) {
        this.sourceField = sourceField;
        this.targetProperty = targetProperty;
        this.required = required;
    }

    // Getters and Setters
    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public String getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getMultiValue() {
        return multiValue;
    }

    public void setMultiValue(Boolean multiValue) {
        this.multiValue = multiValue;
    }
}
