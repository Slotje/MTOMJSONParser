package nl.overheid.ecm.mtom.parser;

import nl.overheid.ecm.mtom.exception.MTOMParsingException;
import nl.overheid.ecm.mtom.exception.ErrorCode;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatic MTOM parser that extracts all fields from MTOM XML
 * without requiring client configuration.
 * This is a proof of concept demonstrating MTOM to JSON conversion.
 */
public class AutomaticMTOMParser {

    /**
     * Parse MTOM XML and automatically extract all fields
     *
     * @param mtomXml The MTOM XML content
     * @return Map containing all extracted fields
     * @throws MTOMParsingException if parsing fails
     */
    public Map<String, Object> parse(String mtomXml) throws MTOMParsingException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(mtomXml.getBytes()));

            XPath xpath = XPathFactory.newInstance().newXPath();
            Map<String, Object> result = new HashMap<>();

            // Extract ecmid if present
            String ecmid = (String) xpath.evaluate("//ecmid", document, XPathConstants.STRING);
            if (ecmid != null && !ecmid.trim().isEmpty()) {
                result.put("ecmid", ecmid.trim());
            }

            // Extract filename if present
            String filename = (String) xpath.evaluate("//filename", document, XPathConstants.STRING);
            if (filename != null && !filename.trim().isEmpty()) {
                result.put("filename", filename.trim());
            }

            // Extract all value elements with key attributes
            NodeList valueNodes = (NodeList) xpath.evaluate("//value[@key]", document, XPathConstants.NODESET);

            for (int i = 0; i < valueNodes.getLength(); i++) {
                Element element = (Element) valueNodes.item(i);
                String key = element.getAttribute("key");
                String value = element.getTextContent();

                if (key != null && !key.trim().isEmpty()) {
                    // Check if this key already exists (multi-value field)
                    if (result.containsKey(key)) {
                        Object existing = result.get(key);
                        List<String> values;

                        if (existing instanceof List) {
                            values = (List<String>) existing;
                        } else {
                            values = new ArrayList<>();
                            values.add(existing.toString());
                        }

                        if (value != null && !value.trim().isEmpty()) {
                            values.add(value.trim());
                        }
                        result.put(key, values);
                    } else {
                        if (value != null && !value.trim().isEmpty()) {
                            result.put(key, value.trim());
                        }
                    }
                }
            }

            // Extract objectStore if present
            String objectStore = (String) xpath.evaluate("//objectStore", document, XPathConstants.STRING);
            if (objectStore != null && !objectStore.trim().isEmpty()) {
                result.put("objectStore", objectStore.trim());
            }

            // Extract documentClass if present
            String documentClass = (String) xpath.evaluate("//documentClass", document, XPathConstants.STRING);
            if (documentClass != null && !documentClass.trim().isEmpty()) {
                result.put("documentClass", documentClass.trim());
            }

            return result;

        } catch (Exception e) {
            throw new MTOMParsingException(
                ErrorCode.PARSE_001,
                "Failed to parse MTOM XML: " + e.getMessage(),
                e
            );
        }
    }
}
