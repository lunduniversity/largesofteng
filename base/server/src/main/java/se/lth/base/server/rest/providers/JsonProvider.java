package se.lth.base.server.rest.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * This class converts all objects in the REST API to/from JSON using Gson.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
@Provider
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class JsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> multivaluedMap, InputStream entityStream)
            throws IOException, WebApplicationException {
        try (InputStreamReader streamReader = new InputStreamReader(entityStream, "UTF-8");) {
            return GSON.fromJson(streamReader, type);
        }
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(Object o, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> multivaluedMap, OutputStream entityStream)
            throws IOException, WebApplicationException {
        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF-8");) {
            GSON.toJson(o, writer);
        }
    }
}
