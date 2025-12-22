package nl.overheid.ecm.mtom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Information about the parsing process
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParsingInfo {
    private Instant timestamp;
    private String parserVersion;
    private Long processingTimeMs;

    public ParsingInfo() {
        this.timestamp = Instant.now();
        this.parserVersion = "1.0.0";
    }

    public ParsingInfo(String parserVersion) {
        this.timestamp = Instant.now();
        this.parserVersion = parserVersion;
    }

    // Getters and Setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getParserVersion() {
        return parserVersion;
    }

    public void setParserVersion(String parserVersion) {
        this.parserVersion = parserVersion;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
}
