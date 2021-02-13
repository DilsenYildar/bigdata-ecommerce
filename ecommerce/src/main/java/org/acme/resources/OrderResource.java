package org.acme.resources;

import org.acme.models.Order;
import org.acme.services.OrderService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Path("/order")
public class OrderResource {

    @Inject
    OrderService service;

    @GET
    public List<Order> getOrderList() {
        return service.list();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String createOrder(Order order) {
        service.add(order);// todo...
        return "created order";
    }

    // todo...
}