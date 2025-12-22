package nl.overheid.ecm.mtom.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.overheid.ecm.mtom.model.ClientConfiguration;
import nl.overheid.ecm.mtom.service.ConfigurationService;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * REST API for managing client configurations
 */
@Path("/api/v1/config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationResource {
    private static final Logger LOG = Logger.getLogger(ConfigurationResource.class);

    @Inject
    ConfigurationService configurationService;

    /**
     * Get all client configurations
     *
     * GET /api/v1/config
     */
    @GET
    public Response getAllConfigurations() {
        LOG.debug("Fetching all client configurations");
        Map<String, ClientConfiguration> configs = configurationService.getAllConfigurations();
        return Response.ok(configs).build();
    }

    /**
     * Get client configuration by ID
     *
     * GET /api/v1/config/{clientId}
     */
    @GET
    @Path("/{clientId}")
    public Response getConfiguration(@PathParam("clientId") String clientId) {
        LOG.debugf("Fetching configuration for client: %s", clientId);
        ClientConfiguration config = configurationService.getConfiguration(clientId);
        return Response.ok(config).build();
    }

    /**
     * Create or update client configuration
     *
     * POST /api/v1/config
     */
    @POST
    public Response saveConfiguration(@Valid ClientConfiguration config) {
        LOG.infof("Saving configuration for client: %s", config.getClientId());
        configurationService.saveConfiguration(config);
        return Response.status(Response.Status.CREATED)
            .entity(config)
            .build();
    }

    /**
     * Update client configuration
     *
     * PUT /api/v1/config/{clientId}
     */
    @PUT
    @Path("/{clientId}")
    public Response updateConfiguration(
            @PathParam("clientId") String clientId,
            @Valid ClientConfiguration config) {

        LOG.infof("Updating configuration for client: %s", clientId);

        // Ensure client ID matches
        if (!clientId.equals(config.getClientId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Client ID in path and body must match\"}")
                .build();
        }

        configurationService.saveConfiguration(config);
        return Response.ok(config).build();
    }

    /**
     * Delete client configuration
     *
     * DELETE /api/v1/config/{clientId}
     */
    @DELETE
    @Path("/{clientId}")
    public Response deleteConfiguration(@PathParam("clientId") String clientId) {
        LOG.infof("Deleting configuration for client: %s", clientId);
        configurationService.deleteConfiguration(clientId);
        return Response.noContent().build();
    }

    /**
     * Reload client configuration
     *
     * POST /api/v1/config/{clientId}/reload
     */
    @POST
    @Path("/{clientId}/reload")
    public Response reloadConfiguration(@PathParam("clientId") String clientId) {
        LOG.infof("Reloading configuration for client: %s", clientId);
        configurationService.reloadConfiguration(clientId);
        return Response.ok()
            .entity("{\"status\": \"reloaded\", \"clientId\": \"" + clientId + "\"}")
            .build();
    }

    /**
     * Reload all configurations
     *
     * POST /api/v1/config/reload-all
     */
    @POST
    @Path("/reload-all")
    public Response reloadAllConfigurations() {
        LOG.info("Reloading all configurations");
        configurationService.reloadAllConfigurations();
        return Response.ok()
            .entity("{\"status\": \"reloaded\", \"message\": \"All configurations reloaded\"}")
            .build();
    }
}
