package nl.overheid.ecm.mtom.parser;

import jakarta.enterprise.context.ApplicationScoped;
import nl.overheid.ecm.mtom.exception.MTOMParsingException;
import nl.overheid.ecm.mtom.exception.ErrorCode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple MTOM parser - extracts all data from XML and puts it in JSON with same names
 */
@ApplicationScoped
public class AutomaticMTOMParser {

    /**
     * Parse MTOM XML - extract all elements and put in JSON
     */
    public Map<String, Object> parse(String mtomXml) throws MTOMParsingException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(mtomXml.getBytes("UTF-8")));

            Map<String, Object> result = new HashMap<>();

            // Extract all elements from the XML
            extractAllElements(document.getDocumentElement(), result);

            return result;

        } catch (Exception e) {
            throw new MTOMParsingException(
                ErrorCode.PARSE_001,
                "Failed to parse MTOM XML: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Recursively extract all elements from XML node
     */
    private void extractAllElements(Node node, Map<String, Object> result) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String tagName = element.getTagName();

            // Remove namespace prefix if present (e.g., "ecm:value" -> "value")
            if (tagName.contains(":")) {
                tagName = tagName.substring(tagName.indexOf(":") + 1);
            }

            // Check if element has text content (no child elements)
            NodeList children = element.getChildNodes();
            boolean hasElementChildren = false;
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    hasElementChildren = true;
                    break;
                }
            }

            if (!hasElementChildren) {
                // Leaf node - extract text content
                String textContent = element.getTextContent().trim();
                if (!textContent.isEmpty()) {
                    String jsonKey = tagName;

                    // Special handling for <value key="X">Y</value> pattern
                    if ("value".equals(tagName) && element.hasAttribute("key")) {
                        jsonKey = element.getAttribute("key");
                    }

                    // Check if this key already exists (multi-value field)
                    if (result.containsKey(jsonKey)) {
                        Object existing = result.get(jsonKey);
                        List<String> values;

                        if (existing instanceof List) {
                            values = (List<String>) existing;
                        } else {
                            values = new ArrayList<>();
                            values.add(existing.toString());
                        }
                        values.add(textContent);
                        result.put(jsonKey, values);
                    } else {
                        result.put(jsonKey, textContent);
                    }
                }
            } else {
                // Has child elements - recurse
                for (int i = 0; i < children.getLength(); i++) {
                    extractAllElements(children.item(i), result);
                }
            }
        }
    }
}
