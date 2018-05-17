package se.lth.base.server.rest;

import se.lth.base.server.Config;
import se.lth.base.server.data.Foo;
import se.lth.base.server.data.FooDataAccess;
import se.lth.base.server.data.Role;
import se.lth.base.server.data.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.util.List;

@Path("foo")
public class FooResource {

    private final FooDataAccess fooDao = new FooDataAccess(Config.instance().getDatabaseDriver());
    private final User user;

    public FooResource(@Context ContainerRequestContext context) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public Foo addFoo(Foo foo) throws URISyntaxException {
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

    @POST
    @RolesAllowed(Role.Names.USER)
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Path("{fooId}/total")
    public int updateFoo(@PathParam("fooId") int fooId, int totalDelta) {
        return fooDao.updateTotal(fooId, user.getId(), totalDelta);
    }

    @DELETE
    @RolesAllowed(Role.Names.USER)
    @Path("{fooId}")
    public void deleteFoo(@PathParam("fooId") int fooId) {
        fooDao.deleteFoo(fooId, user.getId());
    }
}
