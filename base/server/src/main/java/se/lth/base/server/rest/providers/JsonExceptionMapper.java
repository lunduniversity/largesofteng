package se.lth.base.server.rest.providers;

import org.eclipse.jetty.util.log.StdErrLog;
import se.lth.base.server.BaseServer;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.ErrorType;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
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
            StdErrLog.getLogger(BaseServer.class).warn(exception.getMessage(), exception);
        }
        return Response.status(status)
                .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=utf-8")
                .entity(jsonObject)
                .build();
    }
}
