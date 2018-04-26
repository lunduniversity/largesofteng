package se.lth.base.server.rest.providers;

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
import java.util.logging.Logger;

@Provider
public class JsonExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        ErrorType errorType = ErrorType.UNKNOWN;
        int status = 500;
        if (exception instanceof DataAccessException) {
            DataAccessException dataAccessException = (DataAccessException) exception;
            errorType = dataAccessException.getErrorType();
            status = 400;
        }
        if (exception instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) exception;
            status = webApplicationException.getResponse().getStatus();
        }
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("error", errorType);
        jsonObject.put("message", exception.getMessage());
        jsonObject.put("status", status);
        Logger.getLogger(BaseServer.class.getSimpleName()).log(errorType.logLevel(), exception.getMessage(), exception);
        return Response.status(status)
                .header("Content-Type", MediaType.APPLICATION_JSON + ";charset=utf-8")
                .entity(jsonObject)
                .build();
    }
}
