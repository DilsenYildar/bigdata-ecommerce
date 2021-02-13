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
    public String getShoppingCart(@PathParam("key") String key) {
        String cart = shoppingCartService.get(key);
        return cart != null
                ? cart
                : "could not found item";
    }

    @DELETE
    @Path("/{key}/{name}")
    public String removeItemFromCart(@PathParam("key") String key, @PathParam("name") String name) {
        return shoppingCartService.remove(key, name)
                ? "successfully updated cart"
                : "could not updated";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String addItemToShoppingCart(Item item) {
        return shoppingCartService.addValueToKey(item.getUsername(), item)
                ? "successfully add item to cart"
                : "unable to add";
    }

}
