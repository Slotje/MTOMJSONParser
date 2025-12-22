package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.Map;

/**
 * Result of MTOM to JSON conversion
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParsedMessage {
    private String messageId;
    private String clientId;
    private Map<String, Object> metadata;
    private MessageContent content;
    private ParsingInfo parsingInfo;

    public ParsedMessage() {
        this.metadata = new HashMap<>();
        this.parsingInfo = new ParsingInfo();
    }

    public ParsedMessage(String messageId, String clientId) {
        this.messageId = messageId;
        this.clientId = clientId;
        this.metadata = new HashMap<>();
        this.parsingInfo = new ParsingInfo();
    }

    // Getters and Setters
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public ParsingInfo getParsingInfo() {
        return parsingInfo;
    }

    public void setParsingInfo(ParsingInfo parsingInfo) {
        this.parsingInfo = parsingInfo;
    }
}
