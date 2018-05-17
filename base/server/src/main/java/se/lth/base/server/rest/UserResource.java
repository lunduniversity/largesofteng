package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Path("user")
public class UserResource {

    public static final String USER_TOKEN = "USER_TOKEN";

    private final ContainerRequestContext context;
    private final User user;
    private final Session session;
    private final UserDataAccess userDao = new UserDataAccess(Config.instance().getDatabaseDriver());

    public UserResource(@Context ContainerRequestContext context) {
        this.context = context;
        this.user = (User) context.getProperty(User.class.getSimpleName());
        this.session = (Session) context.getProperty(Session.class.getSimpleName());
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
    public Response login(Credentials credentials,
                          @QueryParam("remember") @DefaultValue("false") boolean rememberMe)
            throws URISyntaxException {
        Session newSession = userDao.authenticate(credentials);
        int maxAge = rememberMe ? (int) TimeUnit.DAYS.toSeconds(7) : NewCookie.DEFAULT_MAX_AGE;
        return Response.noContent().cookie(newCookie(newSession.getSessionId().toString(), maxAge, null)).build();
    }

    private NewCookie newCookie(String value, int maxAge, Date expiry) {
        return new NewCookie(USER_TOKEN,
                value,                                          // value
                "/rest",                                        // path
                context.getUriInfo().getBaseUri().getHost(),    // host
                NewCookie.DEFAULT_VERSION,                      // version
                "",                                             // comment
                maxAge,                                         // max-age
                expiry,                                         // expiry
                false,                                          // secure
                true);                                          // http-onle

    }

    @Path("logout")
    @POST
    @PermitAll
    public Response logout() {
        userDao.removeSession(session.getSessionId());
        return Response.noContent().cookie(newCookie("", 0, new Date(0L))).build();
    }

    @Path("roles")
    @GET
    @RolesAllowed(Role.Names.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Set<Role> getRoles() {
        return Role.ALL_ROLES;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.ADMIN)
    public User createUser(Credentials credentials) {
        if (!credentials.hasPassword() || !credentials.validPassword()) {
            throw new WebApplicationException("Password too short", Response.Status.BAD_REQUEST);
        }
        return userDao.addUser(credentials);
    }

    @Path("all")
    @GET
    @RolesAllowed(Role.Names.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Path("{id}")
    @GET
    @RolesAllowed(Role.Names.ADMIN)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public User getUser(@PathParam("id") int userId) {
        return userDao.getUser(userId);
    }

    @Path("{id}")
    @RolesAllowed(Role.Names.ADMIN)
    @PUT
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public User putUser(@PathParam("id") int userId, Credentials credentials) {
        if (credentials.hasPassword() && !credentials.validPassword()) {
            throw new WebApplicationException("Password too short", Response.Status.BAD_REQUEST);
        }
        if (userId == user.getId() && user.getRole().getLevel() > credentials.getRole().getLevel()) {
            throw new WebApplicationException("Cant't demote yourself", Response.Status.BAD_REQUEST);
        }
        return userDao.updateUser(userId, credentials);
    }

    @Path("{id}")
    @RolesAllowed(Role.Names.ADMIN)
    @DELETE
    public void deleteUser(@PathParam("id") int userId) {
        if (userId == currentUser().getId()) {
            throw new WebApplicationException("Don't delete yourself", Response.Status.BAD_REQUEST);
        }
        if (!userDao.deleteUser(userId)) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
    }
}
