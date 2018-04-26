package se.lth.base.server.rest.providers;

import org.glassfish.hk2.api.Factory;
import se.lth.base.server.data.UserSession;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

public class UserSessionFactory implements Factory<UserSession> {
    private ContainerRequestContext requestContext;

    @Inject
    public UserSessionFactory(ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }


    @Override
    public UserSession provide() {
        return (UserSession) requestContext.getProperty(UserSession.class.getSimpleName());
    }

    @Override
    public void dispose(UserSession instance) {
    }
}
