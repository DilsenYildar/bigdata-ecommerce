package org.acme.resources;

import com.google.gson.Gson;
import org.acme.models.User;
import org.acme.services.SessionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/session")
public class SessionResource {

    @Inject
    SessionService service;

    @GET
    @Path("/{username}")
    public String getSession(@PathParam("username") String username) throws Exception {
        String password = service.get(username);
        return password != null && !password.isEmpty()
                ? new Gson().toJson(new User(username, password))
                : "session data not found";
    }

    @DELETE
    @Path("/{username}")
    public String logout(@PathParam("username") String username) {
        return service.remove(username)
                ? "successfully logout"
                : "could not logout";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(User user) {
        return service.set(user.getUsername(), user.getPassword())
                ? "successfully login"
                : "unable to logout";
    }

}