package se.lth.base.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import se.lth.base.server.data.SimpleDataAccess;
import se.lth.base.server.data.UserDataAccess;
import se.lth.base.server.data.User;
import se.lth.base.server.data.UserSession;
import se.lth.base.server.rest.providers.UserFactory;
import se.lth.base.server.rest.providers.UserSessionFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BaseServer {

    public static AbstractBinder dataAccessBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new UserDataAccess(Config.DATABASE_DRIVER)).to(UserDataAccess.class);
                bind(new SimpleDataAccess(Config.DATABASE_DRIVER)).to(SimpleDataAccess.class);
                bind(Config.SERVER_HOST + Config.REST_PREFIX).named("baseUrl").to(String.class);
                bind(Config.SERVER_HOST).named("host").to(String.class);
                bindFactory(UserFactory.class).proxy(true).proxyForSameScope(false).to(User.class).in(RequestScoped.class);
                bindFactory(UserSessionFactory.class).proxy(true).proxyForSameScope(false).to(UserSession.class).in(RequestScoped.class);
            }
        };
    }

    public static void main(String[] args) {

        Server server = new Server(Config.PORT);

        // Configure rest jersey servlet
        ResourceConfig jerseyConfig = new ResourceConfig();
        jerseyConfig.register(dataAccessBinder());
        jerseyConfig.packages(BaseServer.class.getPackage().getName());
        jerseyConfig.register(RolesAllowedDynamicFeature.class);

        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(jerseyConfig));
        ServletContextHandler jerseyContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        jerseyContext.addServlet(jerseyServlet, Config.REST_PREFIX + "/*");

        // Configure HTML/CSS/JS files
        ResourceHandler resources = new ResourceHandler();
        resources.setResourceBase("/");
        resources.setBaseResource(Resource.newClassPathResource("/webassets"));
        resources.setWelcomeFiles(new String[0]);
        resources.setCacheControl("max-age=0,public"); // Disabled cache for easier development

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{new IndexHandler(), resources, jerseyContext});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            Logger.getLogger(BaseServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            server.destroy();
        }
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private static class IndexHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
            if (target.equals("/")) {
                response.addHeader("Content-Type", "text/html");
                copyStream(BaseServer.class.getResourceAsStream("/webassets/index.html"), response.getOutputStream());
                baseRequest.setHandled(true);
            }
        }
    }
}
