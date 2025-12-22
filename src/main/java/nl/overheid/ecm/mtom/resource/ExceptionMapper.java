package nl.overheid.ecm.mtom.resource;

import jakarta.ws.rs.core.Response;
import nl.overheid.ecm.mtom.exception.*;
import org.jboss.logging.Logger;

/**
 * Exception mapper for REST API
 */
@jakarta.ws.rs.ext.Provider
public class ExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(ExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Exception occurred", exception);

        if (exception instanceof MTOMParsingException) {
            MTOMParsingException e = (MTOMParsingException) exception;
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getErrorDetails())
                .build();
        }

        if (exception instanceof ValidationException) {
            ValidationException e = (ValidationException) exception;
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getErrorDetails())
                .build();
        }

        if (exception instanceof ConfigurationException) {
            ConfigurationException e = (ConfigurationException) exception;
            // Configuration errors are internal server errors
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getErrorDetails())
                .build();
        }

        if (exception instanceof TechnicalException) {
            TechnicalException e = (TechnicalException) exception;
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getErrorDetails())
                .build();
        }

        // Generic error
        ErrorDetails errorDetails = ErrorDetails.of(
            ErrorCode.TECH_004,
            exception.getMessage() != null ? exception.getMessage() : "Unexpected error"
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(errorDetails)
            .build();
    }
}
