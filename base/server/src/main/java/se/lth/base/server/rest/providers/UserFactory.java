package se.lth.base.server.rest.providers;

import org.glassfish.hk2.api.Factory;
import se.lth.base.server.data.User;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

public class UserFactory implements Factory<User> {
    private ContainerRequestContext requestContext;

    @Inject
    public UserFactory(ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }


    @Override
    public User provide() {
        return (User) requestContext.getProperty(User.class.getSimpleName());
    }

    @Override
    public void dispose(User instance) {
    }
}
