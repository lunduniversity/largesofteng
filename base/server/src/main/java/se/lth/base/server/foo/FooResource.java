package se.lth.base.server.foo;

import se.lth.base.server.Config;
import se.lth.base.server.user.Role;
import se.lth.base.server.user.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("foo")
public class FooResource {

    // Foo data access wraps all database calls relating to the foo table.
    // The database URL is statically available.
    private final FooDataAccess fooDao = new FooDataAccess(Config.instance().getDatabaseDriver());

    // Current user, it is set by AuthenticationFilter. Based on a database lookup of the users login token.
    private final User user;

    public FooResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public Foo addFoo(Foo foo) {
        return fooDao.addFoo(user.getId(), foo.getPayload());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public List<Foo> getFoos() {
        return fooDao.getUsersFoo(user.getId());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.ADMIN)
    @Path("user/{userId}")
    public List<Foo> getUsersFoos(@PathParam("userId") int userId) {
        return fooDao.getUsersFoo(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.ADMIN)
    @Path("all")
    public List<Foo> getAllFoos() {
        return fooDao.getAllFoo();
    }
}
