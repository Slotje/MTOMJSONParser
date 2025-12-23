package nl.overheid.ecm.mtom.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.overheid.ecm.mtom.model.ParsedMessage;
import nl.overheid.ecm.mtom.service.MTOMConversionService;
import org.jboss.logging.Logger;

/**
 * REST API for MTOM to JSON conversion
 */
@Path("/api/v1/mtom")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_XML)
public class MTOMConversionResource {
    private static final Logger LOG = Logger.getLogger(MTOMConversionResource.class);

    @Inject
    MTOMConversionService conversionService;

    /**
     * Convert MTOM message to simple JSON (only mapped fields)
     *
     * POST /api/v1/mtom/convert?clientId=xxx
     * Body: MTOM XML
     */
    @POST
    @Path("/convert")
    public Response convertMTOM(
            @QueryParam("clientId") String clientId,
            String mtomXml) {

        LOG.infof("Received MTOM conversion request for client: %s", clientId);

        if (clientId == null || clientId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"clientId query parameter is required\"}")
                .build();
        }

        if (mtomXml == null || mtomXml.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"MTOM XML body is required\"}")
                .build();
        }

        // Return simple JSON with only mapped metadata fields
        java.util.Map<String, Object> result = conversionService.convertToSimpleJson(mtomXml, clientId);

        return Response.ok(result).build();
    }

    /**
     * Validate MTOM message without full conversion
     *
     * POST /api/v1/mtom/validate?clientId=xxx
     * Body: MTOM XML
     */
    @POST
    @Path("/validate")
    public Response validateMTOM(
            @QueryParam("clientId") String clientId,
            String mtomXml) {

        LOG.infof("Received MTOM validation request for client: %s", clientId);

        if (clientId == null || clientId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"clientId query parameter is required\"}")
                .build();
        }

        if (mtomXml == null || mtomXml.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"MTOM XML body is required\"}")
                .build();
        }

        conversionService.validateMessage(mtomXml, clientId);

        return Response.ok()
            .entity("{\"status\": \"valid\", \"message\": \"MTOM message is valid\"}")
            .build();
    }

    /**
     * Automatically parse MTOM XML without client configuration.
     * This is a proof of concept endpoint that extracts all fields from MTOM structure.
     *
     * POST /api/v1/mtom/parse
     * Body: MTOM XML
     */
    @POST
    @Path("/parse")
    public Response parseMTOM(String mtomXml) {

        LOG.info("Received automatic MTOM parsing request (configuration-free)");

        if (mtomXml == null || mtomXml.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"MTOM XML body is required\"}")
                .build();
        }

        java.util.Map<String, Object> result = conversionService.parseAutomatically(mtomXml);

        return Response.ok(result).build();
    }
}
