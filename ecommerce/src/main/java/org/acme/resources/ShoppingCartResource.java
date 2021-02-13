package org.acme.resources;

import org.acme.services.ShoppingCartService;
import org.acme.models.Item;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/shopping-cart")
public class ShoppingCartResource {

    @Inject
    ShoppingCartService shoppingCartService;

    @GET
    @Path("/{key}")
    public String getShoppingCart(@PathParam("key") String username) {
        String cart = shoppingCartService.get(username);
        return cart != null
                ? cart
                : "could not found item";
    }

    @DELETE
    @Path("/{key}/{name}")
    public String removeItemFromCart(@PathParam("key") String username, @PathParam("name") String name) {
        return shoppingCartService.remove(username, name)
                ? "successfully updated cart"
                : "could not updated";
    }

    @POST
    @Path("/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addItemToShoppingCart(@PathParam("key") String username, Item item) {
        return shoppingCartService.addValueToKey(username, item)
                ? "successfully add item to cart"
                : "unable to add";
    }

}
