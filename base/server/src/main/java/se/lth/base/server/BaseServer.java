package se.lth.base.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import se.lth.base.server.database.CreateSchema;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BaseServer {

    public static void main(String[] args) {
        String databaseDriver = Config.instance().getDatabaseDriver();
        if (new CreateSchema(databaseDriver).createSchemaIfNotExists()) {
            StdErrLog.getLogger(BaseServer.class).info("Installed database to " + databaseDriver);
        }

        Server server = new Server(Config.instance().getPort());

        server.setRequestLog((request, response) -> StdErrLog.getLogger(BaseServer.class).info(
                request.getMethod() + " " + request.getOriginalURI() + " " + response.getStatus()));

        // Handlers take care of server request in the order given
        HandlerList handlers = new HandlerList(
                indexHandler(),
                staticContentHandler(),
                jerseyHandler(jerseyConfig()));
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            StdErrLog.getLogger(BaseServer.class).warn(ex);
        } finally {
            server.destroy();
        }
    }

    static Handler indexHandler() {
        return new AbstractHandler() {

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                if (target.equals("/")) {
                    response.addHeader("Content-Type", "text/html");
                    copyStream(BaseServer.class.getResourceAsStream("/webassets/index.html"), response.getOutputStream());
                    baseRequest.setHandled(true);
                }
            }

            private void copyStream(InputStream input, OutputStream output) throws IOException {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        };
    }

    static ResourceConfig jerseyConfig() {
        ResourceConfig jerseyConfig = new ResourceConfig();
        jerseyConfig.packages(BaseServer.class.getPackage().getName());
        jerseyConfig.register(RolesAllowedDynamicFeature.class);
        return jerseyConfig;
    }


    static Handler jerseyHandler(ResourceConfig resourceConfig) {
        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(resourceConfig));
        ServletContextHandler jerseyContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        jerseyContext.addServlet(jerseyServlet, "/rest" + "/*");
        return jerseyContext;
    }

    static Handler staticContentHandler() {
        ResourceHandler resources = new ResourceHandler();
        resources.setResourceBase("/");
        resources.setBaseResource(Resource.newClassPathResource("/webassets"));
        resources.setWelcomeFiles(new String[0]);
        resources.setCacheControl("max-age=0,public"); // Disabled cache for easier development
        return resources;
    }
}
