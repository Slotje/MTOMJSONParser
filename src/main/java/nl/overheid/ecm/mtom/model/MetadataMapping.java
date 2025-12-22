package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata mapping configuration for MTOM to FileNet conversion
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataMapping {
    @NotBlank(message = "objectStore is required")
    private String objectStore;

    @NotBlank(message = "documentClass is required")
    private String documentClass;

    @NotEmpty(message = "fields cannot be empty")
    @Valid
    private List<FieldMapping> fields = new ArrayList<>();

    public MetadataMapping() {
    }

    public MetadataMapping(String objectStore, String documentClass) {
        this.objectStore = objectStore;
        this.documentClass = documentClass;
    }

    // Getters and Setters
    public String getObjectStore() {
        return objectStore;
    }

    public void setObjectStore(String objectStore) {
        this.objectStore = objectStore;
    }

    public String getDocumentClass() {
        return documentClass;
    }

    public void setDocumentClass(String documentClass) {
        this.documentClass = documentClass;
    }

    public List<FieldMapping> getFields() {
        return fields;
    }

    public void setFields(List<FieldMapping> fields) {
        this.fields = fields;
    }

    public void addField(FieldMapping field) {
        this.fields.add(field);
    }
}
