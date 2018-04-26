package se.lth.base.server.rest;

import se.lth.base.server.data.SimpleDataAccess;
import se.lth.base.server.data.SimpleData;
import se.lth.base.server.data.User;
import se.lth.base.server.data.UserRole;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("simple")
public class SimpleResource {

    private final SimpleDataAccess simpleDao;
    private final User user;

    @Inject
    public SimpleResource(User user, SimpleDataAccess simpleDao) {
        this.user = user;
        this.simpleDao = simpleDao;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(UserRole.USER)
    public SimpleData addData(SimpleData data) {
        return simpleDao.addData(user.getId(), data.getData());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(UserRole.USER)
    public List<SimpleData> getData() {
        return simpleDao.getUsersData(user.getId());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(UserRole.ADMIN)
    @Path("{userId}")
    public List<SimpleData> getUsersData(@PathParam("userId") int userId) {
        return simpleDao.getUsersData(userId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @RolesAllowed(UserRole.ADMIN)
    @Path("all")
    public List<SimpleData> getUsersData() {
        return simpleDao.getAllData();
    }
}
