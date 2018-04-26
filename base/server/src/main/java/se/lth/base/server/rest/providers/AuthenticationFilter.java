package se.lth.base.server.rest.providers;

import se.lth.base.server.Config;
import se.lth.base.server.data.UserDataAccess;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.database.ErrorType;
import se.lth.base.server.data.User;
import se.lth.base.server.data.UserSession;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

/**
 * Adds the UserSession to the current request. This is done by extracting the token in the users cookie and
 * checking the database for the cookie.
 *
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Cookie cookie = requestContext.getCookies().get(Config.USER_TOKEN_COOKIE);
        UserSession userSession = new UserSession(null, User.NONE);
        if (cookie != null) {
            UUID uuid = UUID.fromString(cookie.getValue());
            try {
                userSession = new UserDataAccess(Config.DATABASE_DRIVER).getSession(uuid);
            } catch (DataAccessException e) {
                if (e.getErrorType() != ErrorType.DATA_QUALITY) {
                    throw e;
                }
            }
        }
        requestContext.setProperty(UserSession.class.getSimpleName(), userSession);
        requestContext.setProperty(User.class.getSimpleName(), userSession.getUser());
        requestContext.setSecurityContext(new UserSessionSecurityContext(userSession));
    }

    private static class UserSessionSecurityContext implements SecurityContext {
        private final UserSession userSession;

        public UserSessionSecurityContext(UserSession userSession) {
            this.userSession = userSession;
        }

        @Override
        public Principal getUserPrincipal() {
            return userSession.getUser();
        }

        @Override
        public boolean isUserInRole(String role) {
            return userSession.getUser().getRoles().contains(role);
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public String getAuthenticationScheme() {
            return SecurityContext.FORM_AUTH;
        }
    }
}
