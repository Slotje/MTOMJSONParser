package nl.overheid.ecm.mtom.parser;

import jakarta.enterprise.context.ApplicationScoped;
import nl.overheid.ecm.mtom.exception.ErrorCode;
import nl.overheid.ecm.mtom.exception.MTOMParsingException;
import nl.overheid.ecm.mtom.model.ClientConfiguration;
import nl.overheid.ecm.mtom.model.FieldMapping;
import nl.overheid.ecm.mtom.model.MessageContent;
import nl.overheid.ecm.mtom.model.ParsedMessage;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Parser for MTOM (Message Transmission Optimization Mechanism) messages
 */
@ApplicationScoped
public class MTOMParser {
    private static final Logger LOG = Logger.getLogger(MTOMParser.class);

    private final XPathFactory xPathFactory;
    private final DocumentBuilderFactory documentBuilderFactory;

    public MTOMParser() {
        this.xPathFactory = XPathFactory.newInstance();
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.documentBuilderFactory.setNamespaceAware(true);
    }

    /**
     * Parse MTOM message and convert to ParsedMessage
     *
     * @param mtomXml MTOM message as XML string
     * @param clientConfig Client configuration with field mappings
     * @return ParsedMessage with extracted metadata and content
     */
    public ParsedMessage parse(String mtomXml, ClientConfiguration clientConfig) {
        long startTime = System.currentTimeMillis();

        try {
            LOG.debugf("Starting MTOM parsing for client: %s", clientConfig.getClientId());

            // Parse XML
            Document document = parseXml(mtomXml);
            XPath xpath = xPathFactory.newXPath();

            // Extract message ID
            String messageId = extractMessageId(document, xpath);
            LOG.debugf("Extracted message ID: %s", messageId);

            // Create ParsedMessage
            ParsedMessage parsedMessage = new ParsedMessage(messageId, clientConfig.getClientId());

            // Extract metadata based on field mappings
            extractMetadata(document, xpath, clientConfig, parsedMessage);

            // Extract content
            extractContent(document, xpath, parsedMessage);

            // Set processing time
            long processingTime = System.currentTimeMillis() - startTime;
            parsedMessage.getParsingInfo().setProcessingTimeMs(processingTime);

            LOG.infof("Successfully parsed MTOM message %s in %d ms", messageId, processingTime);

            return parsedMessage;

        } catch (MTOMParsingException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Unexpected error during MTOM parsing", e);
            throw new MTOMParsingException(
                ErrorCode.PARSE_001,
                "Failed to parse MTOM message: " + e.getMessage(),
                e
            );
        }
    }

    private Document parseXml(String xml) {
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));
            return builder.parse(inputSource);
        } catch (Exception e) {
            LOG.error("Failed to parse XML", e);
            throw new MTOMParsingException(
                ErrorCode.PARSE_001,
                "Invalid XML structure: " + e.getMessage(),
                e
            );
        }
    }

    private String extractMessageId(Document document, XPath xpath) {
        try {
            String messageId = (String) xpath.evaluate("//ecmid", document, XPathConstants.STRING);
            if (messageId == null || messageId.isEmpty()) {
                throw new MTOMParsingException(
                    ErrorCode.PARSE_004,
                    "Missing required field: ecmid (message ID)"
                );
            }
            return messageId;
        } catch (Exception e) {
            throw new MTOMParsingException(
                ErrorCode.PARSE_004,
                "Failed to extract message ID: " + e.getMessage(),
                e
            );
        }
    }

    private void extractMetadata(Document document, XPath xpath,
                                 ClientConfiguration config, ParsedMessage parsedMessage) {
        for (FieldMapping mapping : config.getMetadataMapping().getFields()) {
            try {
                Object value = extractFieldValue(document, xpath, mapping);

                if (value != null) {
                    parsedMessage.addMetadata(mapping.getTargetProperty(), value);
                } else if (mapping.getDefaultValue() != null) {
                    parsedMessage.addMetadata(mapping.getTargetProperty(), mapping.getDefaultValue());
                } else if (Boolean.TRUE.equals(mapping.getRequired())) {
                    throw new MTOMParsingException(
                        ErrorCode.PARSE_004,
                        String.format("Required field missing: %s (XPath: %s)",
                            mapping.getTargetProperty(), mapping.getSourceField())
                    );
                }

            } catch (MTOMParsingException e) {
                throw e;
            } catch (Exception e) {
                LOG.errorf("Error extracting field %s: %s", mapping.getTargetProperty(), e.getMessage());
                if (Boolean.TRUE.equals(mapping.getRequired())) {
                    throw new MTOMParsingException(
                        ErrorCode.PARSE_004,
                        String.format("Failed to extract required field %s: %s",
                            mapping.getTargetProperty(), e.getMessage()),
                        e
                    );
                }
            }
        }
    }

    private Object extractFieldValue(Document document, XPath xpath, FieldMapping mapping)
            throws Exception {

        if (Boolean.TRUE.equals(mapping.getMultiValue())) {
            // Extract multiple values
            NodeList nodes = (NodeList) xpath.evaluate(
                mapping.getSourceField(), document, XPathConstants.NODESET);

            if (nodes.getLength() == 0) {
                return null;
            }

            List<String> values = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                String value = nodes.item(i).getTextContent();
                if (value != null && !value.isEmpty()) {
                    values.add(formatValue(value, mapping.getFormat()));
                }
            }
            return values.isEmpty() ? null : values;

        } else {
            // Extract single value
            String value = (String) xpath.evaluate(
                mapping.getSourceField(), document, XPathConstants.STRING);

            if (value == null || value.isEmpty()) {
                return null;
            }

            return formatValue(value, mapping.getFormat());
        }
    }

    private String formatValue(String value, String format) {
        if (format == null || format.isEmpty()) {
            return value;
        }

        try {
            // Handle date/time formatting
            if (format.contains("yyyy")) {
                // Try parsing as date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate date = LocalDate.parse(value);
                return date.format(formatter);
            }

            // Add more format handlers as needed
            return value;

        } catch (Exception e) {
            LOG.warnf("Failed to format value '%s' with format '%s': %s",
                value, format, e.getMessage());
            return value;
        }
    }

    private void extractContent(Document document, XPath xpath, ParsedMessage parsedMessage) {
        try {
            // Extract base64 encoded content
            String contentData = (String) xpath.evaluate(
                "//content/data", document, XPathConstants.STRING);

            String mimeType = (String) xpath.evaluate(
                "//content/@mimeType", document, XPathConstants.STRING);

            String filename = (String) xpath.evaluate(
                "//content/@filename", document, XPathConstants.STRING);

            if (contentData != null && !contentData.isEmpty()) {
                MessageContent content = new MessageContent(contentData, mimeType);
                content.setFilename(filename);

                // Calculate size
                try {
                    byte[] decodedData = Base64.getDecoder().decode(contentData);
                    content.setSize((long) decodedData.length);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Failed to decode base64 content for size calculation", e);
                }

                parsedMessage.setContent(content);
            } else {
                LOG.warn("No content found in MTOM message");
            }

        } catch (Exception e) {
            LOG.error("Failed to extract content", e);
            throw new MTOMParsingException(
                ErrorCode.PARSE_004,
                "Failed to extract message content: " + e.getMessage(),
                e
            );
        }
    }
}
