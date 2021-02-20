package org.acme.resources;

import org.acme.models.entites.Customer;
import org.acme.repository.CustomerRepository;
import org.acme.services.SessionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static org.acme.db.RedisClient.TOKEN_KEY_PREFIX;

@Produces(MediaType.APPLICATION_JSON)
@Path("/session")
public class SessionResource {

    @Inject
    SessionService service;

     @Inject
    CustomerRepository customerRepository;

    @DELETE
    @Path("/{username}")
    public String logout(@PathParam("username") String username) {
        return service.remove(TOKEN_KEY_PREFIX.concat(username)) ? "successfully logout" : "could not logout";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(Customer customer) throws Exception {
        String response = "unable to login";
        Customer foundCustomer = customerRepository.find("username", customer.getUsername()).firstResult();
        if (foundCustomer != null) {
            String decryptPassword = service.decryptPassword(foundCustomer.getPassword());
            if (decryptPassword.equals(customer.getPassword())) {
                response = "successfully login";
                service.set(TOKEN_KEY_PREFIX.concat(customer.getUsername()), foundCustomer.getPassword());
            } else {
                response = "wrong credentials";
            }
        }
        return response;
    }

}