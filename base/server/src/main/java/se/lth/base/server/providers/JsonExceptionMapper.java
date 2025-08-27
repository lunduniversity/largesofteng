package se.lth.base.server.providers;

import org.slf4j.LoggerFactory;
import se.lth.base.server.BaseServer;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.ErrorType;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * This converts all Exceptions to HTTP responses for the REST API. It has special handling for WebApplicationException
 * and DataAccessException.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
@Provider
public class JsonExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        ErrorType errorType = ErrorType.UNKNOWN;
        int status = 500;
        if (exception instanceof DataAccessException) {
            DataAccessException dataAccessException = (DataAccessException) exception;
            errorType = dataAccessException.getErrorType();
            status = errorType.getHttpCode();
        }
        if (exception instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) exception;
            status = webApplicationException.getResponse().getStatus();
        }
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("error", errorType);
        jsonObject.put("message", exception.getMessage());
        jsonObject.put("status", status);
        if (errorType.getLevel() == Level.SEVERE) {
            LoggerFactory.getLogger(BaseServer.class).warn(exception.getMessage(), exception);
        }
        return Response.status(status)
                .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=utf-8")
                .entity(jsonObject)
                .build();
    }
}
