package org.acme.resources;

import org.acme.models.Order;
import org.acme.services.OrderService;
import org.bson.BsonObjectId;
import org.bson.BsonValue;

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
        BsonValue add = service.add(order);
        return add != null
                ? "created order: " + ((BsonObjectId) add).getValue()
                : "could not created order";
    }

    // todo...
}