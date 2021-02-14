package org.acme.resources;

import org.acme.services.ShoppingCartService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/shopping-cart")
public class ShoppingCartResource {

    @Inject
    ShoppingCartService shoppingCartService;

    @GET
    @Path("/{username}")
    public String getShoppingCart(@PathParam("username") String username) {
        String cart = shoppingCartService.get(username);
        return cart != null
                ? cart
                : "could not found item";
    }

    @DELETE
    @Path("/{username}/{name}")
    public String removeItemFromCart(@PathParam("username") String username, @PathParam("name") String name) {
        return shoppingCartService.remove(username, name)
                ? "successfully updated cart"
                : "could not updated";
    }

    @POST
    @Path("/{username}/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addItemToShoppingCart(@PathParam("username") String username, @PathParam("name") String productName) {
        return shoppingCartService.addCartItem(username, productName)
                ? "successfully add item to cart"
                : "unable to add";
    }

}
