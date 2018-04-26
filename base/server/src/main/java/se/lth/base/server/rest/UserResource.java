package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.UserDataAccess;
import se.lth.base.server.data.User;
import se.lth.base.server.data.UserCredentials;
import se.lth.base.server.data.UserRole;
import se.lth.base.server.data.UserSession;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Path("user")
public class UserResource {

    private final String host;
    private final User user;
    private final UserSession userSession;
    private final UserDataAccess userDao;

    @Inject
    public UserResource(@Named("host") String host, User user, UserSession userSession, UserDataAccess userDao) {
        this.host = host;
        this.user = user;
        this.userSession = userSession;
        this.userDao = userDao;
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public User currentUser() {
        return user;
    }

    @Path("login")
    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response login(UserCredentials credentials,
                          @QueryParam("remember") @DefaultValue("false") boolean rememberMe)
            throws URISyntaxException {
        UserSession newSession = userDao.authenticate(credentials.getUsername(), credentials.getPassword());
        Cookie c = cookie(newSession.getSessionId().toString());
        NewCookie newCookie;
        if (rememberMe) {
            newCookie = new NewCookie(c, "", (int) TimeUnit.DAYS.toSeconds(7), false);
        } else {
            newCookie = new NewCookie(c);
        }
        return Response.noContent().cookie(newCookie).build();
    }

    @Path("logout")
    @POST
    @RolesAllowed(UserRole.USER)
    public Response logout() {
        userDao.removeSession(userSession.getSessionId());
        NewCookie newCookie = new NewCookie(cookie(userSession.getSessionId().toString()), "", 0, false);
        return Response.noContent().cookie(newCookie).build();
    }

    private Cookie cookie(String value) {
        //context.getUriInfo().getBaseUri().getHost());
        return new Cookie(Config.USER_TOKEN_COOKIE, value, "/", host);
    }

    @Path("roles")
    @GET
    @RolesAllowed(UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Set<String> getRoles() {
        return UserRole.ALL_ROLES;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(UserRole.ADMIN)
    public User createUser(UserCredentials credentials) {
        return userDao.addUser(credentials.getUsername(), credentials.getRole(), credentials.getPassword());
    }

    @Path("all")
    @GET
    @RolesAllowed(UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Path("{id}")
    @RolesAllowed(UserRole.ADMIN)
    @GET
    public User getUser(@PathParam("id") int userId) {
        return userDao.getUser(userId);
    }

    @Path("{id}")
    @RolesAllowed(UserRole.ADMIN)
    @DELETE
    public void deleteUser(@PathParam("id") int userId) {
        if (userId == currentUser().getId()) {
            throw new WebApplicationException("Don't delete yourself", 400);
        }
        if (!userDao.deleteUser(userId)) {
            throw new WebApplicationException(404);
        }
    }
}
