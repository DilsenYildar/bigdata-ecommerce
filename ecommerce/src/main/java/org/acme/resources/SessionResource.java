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
    @Path("/{key}")
    public String getSession(@PathParam("key") String key) throws Exception {
        String session = service.get(key);
        return session != null && !session.isEmpty()
                ? new Gson().toJson(new User(key, session))
                : "session data not found";
    }

    @DELETE
    @Path("/{key}")
    public String logout(@PathParam("key") String key) {
        return service.remove(key)
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