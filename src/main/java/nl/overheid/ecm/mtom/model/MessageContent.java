package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents the binary content of a message
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageContent {
    private String data; // Base64 encoded
    private String mimeType;
    private String filename;
    private Long size;

    public MessageContent() {
    }

    public MessageContent(String data, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
    }

    // Getters and Setters
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
