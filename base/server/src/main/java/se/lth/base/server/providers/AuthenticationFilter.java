package se.lth.base.server.providers;

import jakarta.ws.rs.core.SecurityContext;
import se.lth.base.server.Config;
import se.lth.base.server.user.Session;
import se.lth.base.server.user.User;
import se.lth.base.server.user.UserDataAccess;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.ErrorType;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;

/**
 * Adds the Session to the current request. This is done by extracting the token in the users cookie and
 * checking the database for the cookie.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {

    private final UserDataAccess userDataAccess = new UserDataAccess(Config.instance().getDatabaseDriver());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get("USER_TOKEN");
        Session session = new Session(null, User.NONE);
        if (cookie != null) {
            UUID uuid = UUID.fromString(cookie.getValue());
            try {
                session = userDataAccess.getSession(uuid);
            } catch (DataAccessException e) {
                if (e.getErrorType() != ErrorType.NOT_FOUND) {
                    throw e;
                }
            }
        }
        requestContext.setProperty(Session.class.getSimpleName(), session);
        requestContext.setProperty(User.class.getSimpleName(), session.getUser());
        requestContext.setSecurityContext(session);
    }
}
