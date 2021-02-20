package org.acme.resources;

import com.google.gson.GsonBuilder;
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
        return service.getOrders();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String createOrder(Order order) {
        service.saveOrder(order);
        return String.format("created order: %s",  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(order));
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteOrder(@PathParam("id") String id) {
        return service.deleteOrder(id) ? "deleted order successfully" : "unable to delete";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateOrder(Order order) {
        service.updateOrder(order);
        return String.format("created order: %s",  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(order));
    }

    // only used for test.
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public String clearOrderTable() {
        service.deleteOrders();
        return "deleted orders";
    }
}