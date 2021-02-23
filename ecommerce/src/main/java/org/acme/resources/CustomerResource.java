package org.acme.resources;

import com.google.gson.GsonBuilder;
import org.acme.models.entites.Customer;
import org.acme.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/customer")
public class CustomerResource {

    @Inject
    CustomerService service;

    @GET
    @Path("/{username}")
    public String getCustomer(@PathParam("username") String username) {
        Customer customer = service.getByUsername(username);
        return customer != null ? new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
                customer) : "customer data not found";
    }

    @DELETE
    @Path("/{username}")
    public String deleteCustomer(@PathParam("username") String username) {
        return service.deleteCustomer(username) ? "successfully deleted" : "could not deleted";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String createCustomer(Customer customer) {
        service.createCustomer(customer);
        return "successfully created";
    }
}
