package org.acme;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/increments")
public class IncrementResource {

    @Inject
    IncrementService service;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Increment create(Increment increment) {
        service.set(increment.getKey(), increment.getValue());
        return increment;
    }

    @GET
    @Path("/{key}")
    public Increment get(@PathParam("key") String key) {
        return new Increment(key, service.get(key));
    }

}