package se.lth.base.server;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import se.lth.base.server.data.*;
import se.lth.base.server.database.CreateSchema;
import se.lth.base.server.rest.UserResource;
import se.lth.base.server.rest.providers.JsonProvider;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collections;


public class BaseResourceTest extends JerseyTest {

    protected static final User ADMIN = new User(1, Role.ADMIN, "Admin");
    protected static final Credentials ADMIN_CREDENTIALS = new Credentials("Admin", "password", Role.ADMIN);
    protected static final User TEST = new User(2, Role.USER, "Test");
    protected static final Credentials TEST_CREDENTIALS = new Credentials("Test", "password", Role.USER);

    private static final String IN_MEM_DRIVER_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    static {
        Config.instance().setDatabaseDriver(IN_MEM_DRIVER_URL);
    }

    private Session authedSession = null;

    protected void login(Credentials credentials) {
        authedSession = new UserDataAccess(Config.instance().getDatabaseDriver()).authenticate(credentials);
    }

    protected void logout() {
        new UserDataAccess(Config.instance().getDatabaseDriver()).removeSession(authedSession.getSessionId());
        authedSession = null;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JsonProvider.class).register(new CookieAdder());
    }

    @Before
    public void createSchema() {
        new CreateSchema(Config.instance().getDatabaseDriver()).createSchema();
    }

    @After
    public void destroySchema() {
        new CreateSchema(Config.instance().getDatabaseDriver()).dropAll();
    }

    @Override
    protected Application configure() {
        return BaseServer.jerseyConfig();
    }

    @Provider
    private class CookieAdder implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            if (authedSession != null) {
                requestContext.getHeaders().put("Cookie", Collections.singletonList(
                        new Cookie(UserResource.USER_TOKEN, authedSession.getSessionId().toString())
                ));
            }
        }
    }
}
