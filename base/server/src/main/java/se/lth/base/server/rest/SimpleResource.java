package se.lth.base.server.rest;

import se.lth.base.server.data.Role;
import se.lth.base.server.data.Simple;
import se.lth.base.server.data.SimpleDataAccess;
import se.lth.base.server.data.User;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("simple")
public class SimpleResource {

    private final User user;
    private final SimpleDataAccess simpleDao;

    @Inject
    public SimpleResource(ContainerRequestContext context, SimpleDataAccess simpleDao) {
        this.user = (User) context.getProperty(User.class.getSimpleName());
        this.simpleDao = simpleDao;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public Simple addData(Simple simple) {
        return simpleDao.addSimple(user.getId(), simple.getPayload());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.USER)
    public List<Simple> getSimples() {
        return simpleDao.getUsersSimple(user.getId());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.ADMIN)
    @Path("{userId}")
    public List<Simple> getUsersSimple(@PathParam("userId") int userId) {
        return simpleDao.getUsersSimple(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(Role.Names.ADMIN)
    @Path("all")
    public List<Simple> getAllSimple() {
        return simpleDao.getAllSimple();
    }
}
