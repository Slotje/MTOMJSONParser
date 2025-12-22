package nl.overheid.ecm.mtom.parser;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import nl.overheid.ecm.mtom.exception.MTOMParsingException;
import nl.overheid.ecm.mtom.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
class MTOMParserTest {

    @Inject
    MTOMParser parser;

    private ClientConfiguration testConfig;
    private String validMtomXml;

    @BeforeEach
    void setUp() {
        // Create test configuration
        testConfig = new ClientConfiguration();
        testConfig.setClientId("test-client");
        testConfig.setClientName("Test Client");

        MetadataMapping mapping = new MetadataMapping("ECM_ObjectStore", "Document");
        mapping.addField(new FieldMapping("//ecmid", "MessageId", true));
        mapping.addField(new FieldMapping("//value[@key='Eigenaar_L1']", "Owner", true));
        mapping.addField(new FieldMapping("//value[@key='Taal']", "Language", false));
        testConfig.setMetadataMapping(mapping);

        BusinessInfo businessInfo = new BusinessInfo("test@example.com", "Test Group");
        testConfig.setBusinessInfo(businessInfo);

        // Create valid MTOM XML
        validMtomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <message xmlns="http://www.overheid.nl/ecm/mtom">
                <header>
                    <ecmid>test-message-001</ecmid>
                    <timestamp>2025-12-22T12:00:00Z</timestamp>
                </header>
                <metadata>
                    <value key="Eigenaar_L1">CA</value>
                    <value key="Taal">NL</value>
                </metadata>
                <content mimeType="application/pdf" filename="test.pdf">
                    <data>SGVsbG8gV29ybGQh</data>
                </content>
            </message>
            """;
    }

    @Test
    void testParseValidMessage() {
        ParsedMessage result = parser.parse(validMtomXml, testConfig);

        assertThat(result).isNotNull();
        assertThat(result.getMessageId()).isEqualTo("test-message-001");
        assertThat(result.getClientId()).isEqualTo("test-client");
        assertThat(result.getMetadata()).containsEntry("MessageId", "test-message-001");
        assertThat(result.getMetadata()).containsEntry("Owner", "CA");
        assertThat(result.getContent()).isNotNull();
        assertThat(result.getContent().getMimeType()).isEqualTo("application/pdf");
    }

    @Test
    void testParseInvalidXml() {
        String invalidXml = "not valid xml";

        assertThatThrownBy(() -> parser.parse(invalidXml, testConfig))
            .isInstanceOf(MTOMParsingException.class)
            .hasMessageContaining("Invalid XML structure");
    }

    @Test
    void testParseMissingRequiredField() {
        String xmlWithoutOwner = """
            <?xml version="1.0" encoding="UTF-8"?>
            <message xmlns="http://www.overheid.nl/ecm/mtom">
                <header>
                    <ecmid>test-message-001</ecmid>
                </header>
                <metadata>
                </metadata>
                <content mimeType="application/pdf">
                    <data>SGVsbG8gV29ybGQh</data>
                </content>
            </message>
            """;

        assertThatThrownBy(() -> parser.parse(xmlWithoutOwner, testConfig))
            .isInstanceOf(MTOMParsingException.class)
            .hasMessageContaining("Required field missing");
    }

    @Test
    void testParseMissingMessageId() {
        String xmlWithoutId = """
            <?xml version="1.0" encoding="UTF-8"?>
            <message xmlns="http://www.overheid.nl/ecm/mtom">
                <header>
                    <timestamp>2025-12-22T12:00:00Z</timestamp>
                </header>
                <metadata>
                    <value key="Eigenaar_L1">CA</value>
                </metadata>
                <content mimeType="application/pdf">
                    <data>SGVsbG8gV29ybGQh</data>
                </content>
            </message>
            """;

        assertThatThrownBy(() -> parser.parse(xmlWithoutId, testConfig))
            .isInstanceOf(MTOMParsingException.class)
            .hasMessageContaining("ecmid");
    }
}
