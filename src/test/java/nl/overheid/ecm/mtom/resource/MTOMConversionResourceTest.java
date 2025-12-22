package nl.overheid.ecm.mtom.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import nl.overheid.ecm.mtom.model.*;
import nl.overheid.ecm.mtom.service.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class MTOMConversionResourceTest {

    @Inject
    ConfigurationService configurationService;

    private String testClientId = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        // Create and save test configuration
        ClientConfiguration config = new ClientConfiguration();
        config.setClientId(testClientId);
        config.setClientName("Test Client");

        MetadataMapping mapping = new MetadataMapping("ECM_ObjectStore", "Document");
        mapping.addField(new FieldMapping("//ecmid", "MessageId", true));
        mapping.addField(new FieldMapping("//value[@key='Eigenaar_L1']", "Owner", true));
        config.setMetadataMapping(mapping);

        BusinessInfo businessInfo = new BusinessInfo("test@example.com", "Test Group");
        businessInfo.setMaxMessageSize(10485760);
        config.setBusinessInfo(businessInfo);

        ProcessingRules processingRules = new ProcessingRules();
        processingRules.setProcessingEnabled(true);
        config.setProcessingRules(processingRules);

        configurationService.saveConfiguration(config);
    }

    @Test
    void testConvertMTOMSuccess() {
        String mtomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <message xmlns="http://www.overheid.nl/ecm/mtom">
                <header>
                    <ecmid>test-message-001</ecmid>
                </header>
                <metadata>
                    <value key="Eigenaar_L1">CA</value>
                </metadata>
                <content mimeType="application/pdf" filename="test.pdf">
                    <data>SGVsbG8gV29ybGQh</data>
                </content>
            </message>
            """;

        given()
            .contentType(ContentType.XML)
            .queryParam("clientId", testClientId)
            .body(mtomXml)
        .when()
            .post("/api/v1/mtom/convert")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("messageId", equalTo("test-message-001"))
            .body("clientId", equalTo(testClientId))
            .body("metadata.MessageId", equalTo("test-message-001"))
            .body("metadata.Owner", equalTo("CA"));
    }

    @Test
    void testConvertMTOMMissingClientId() {
        given()
            .contentType(ContentType.XML)
            .body("<message/>")
        .when()
            .post("/api/v1/mtom/convert")
        .then()
            .statusCode(400)
            .body(containsString("clientId"));
    }

    @Test
    void testConvertMTOMMissingBody() {
        given()
            .contentType(ContentType.XML)
            .queryParam("clientId", testClientId)
        .when()
            .post("/api/v1/mtom/convert")
        .then()
            .statusCode(400)
            .body(containsString("XML body"));
    }

    @Test
    void testValidateMTOMSuccess() {
        String mtomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <message xmlns="http://www.overheid.nl/ecm/mtom">
                <header>
                    <ecmid>test-message-002</ecmid>
                </header>
                <metadata>
                    <value key="Eigenaar_L1">CA</value>
                </metadata>
                <content mimeType="application/pdf">
                    <data>SGVsbG8gV29ybGQh</data>
                </content>
            </message>
            """;

        given()
            .contentType(ContentType.XML)
            .queryParam("clientId", testClientId)
            .body(mtomXml)
        .when()
            .post("/api/v1/mtom/validate")
        .then()
            .statusCode(200)
            .body("status", equalTo("valid"));
    }
}
