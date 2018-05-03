package se.lth.base.server;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
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

import static se.lth.base.server.database.BaseDataAccessTest.IN_MEM_DRIVER_URL;

public class BaseResourceTest extends JerseyTest {

    public static final User ADMIN = new User(1, Role.ADMIN, "Admin");
    public static final Credentials ADMIN_CREDENTIALS = new Credentials("Admin", "password", Role.ADMIN);
    public static final User TEST = new User(2, Role.USER, "Test");
    public static final Credentials TEST_CREDENTIALS = new Credentials("Test", "password", Role.USER);

    private Session authedSession = null;

    protected void login(Credentials credentials) {
        authedSession = new UserDataAccess(IN_MEM_DRIVER_URL).authenticate(credentials);
    }

    protected void logout() {
        new UserDataAccess(IN_MEM_DRIVER_URL).removeSession(authedSession.getSessionId());
        authedSession = null;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(JsonProvider.class).register(new CookieAdder());
    }

    @Before
    public void createSchema() {
        new CreateSchema(IN_MEM_DRIVER_URL).createSchema();
    }

    @After
    public void destroySchema() {
        new CreateSchema(IN_MEM_DRIVER_URL).dropAll();
    }

    @Override
    protected Application configure() {
        AbstractBinder bindings = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new UserDataAccess(IN_MEM_DRIVER_URL)).to(UserDataAccess.class);
                bind(new SimpleDataAccess(IN_MEM_DRIVER_URL)).to(SimpleDataAccess.class);
            }
        };
        return BaseServer.jerseyConfig(bindings);
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
