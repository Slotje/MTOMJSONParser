package nl.overheid.ecm.mtom.exception;

/**
 * Error codes for structured error handling
 */
public enum ErrorCode {
    // Parsing Errors (PARSE_xxx)
    PARSE_001("PARSE_001", "Invalid XML structure", true),
    PARSE_002("PARSE_002", "Missing namespace", true),
    PARSE_003("PARSE_003", "Corrupted or incomplete message", true),
    PARSE_004("PARSE_004", "Invalid MTOM format", true),

    // Configuration Errors (CONFIG_xxx)
    CONFIG_001("CONFIG_001", "Unknown client ID", false),
    CONFIG_002("CONFIG_002", "Invalid XPath expression", false),
    CONFIG_003("CONFIG_003", "Missing required configuration element", false),
    CONFIG_004("CONFIG_004", "Invalid client configuration", false),

    // Validation Errors (VALID_xxx)
    VALID_001("VALID_001", "Required field missing", true),
    VALID_002("VALID_002", "Invalid field format", true),
    VALID_003("VALID_003", "Business rule violation", true),
    VALID_004("VALID_004", "JSON schema validation failed", true),
    VALID_005("VALID_005", "Message size exceeded", true),

    // Technical Errors (TECH_xxx)
    TECH_001("TECH_001", "Configuration retrieval timeout", false),
    TECH_002("TECH_002", "Memory limit exceeded", false),
    TECH_003("TECH_003", "Connection failure", false),
    TECH_004("TECH_004", "Unexpected system error", false);

    private final String code;
    private final String description;
    private final boolean retryable;

    ErrorCode(String code, String description, boolean retryable) {
        this.code = code;
        this.description = description;
        this.retryable = retryable;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
