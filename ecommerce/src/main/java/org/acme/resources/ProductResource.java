package org.acme.resources;

import com.google.gson.GsonBuilder;
import org.acme.models.entites.Product;
import org.acme.services.ProductService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/product")
public class ProductResource {

    @Inject
    ProductService service;

    @GET
    @Path("/{name}")
    public String getProduct(@PathParam("name") String pName) throws Exception {
        Product product = service.get(pName);
        return product != null ? new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(
                product) : "product data not found";
    }

    @DELETE
    @Path("/{name}")
    public String deleteProduct(@PathParam("name") String pName) {
        return service.remove(pName) ? "successfully deleted" : "could not deleted";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String createProduct(Product product) {
        service.create(product);
        return "successfully created";
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateProduct(@PathParam("name") String name, Product product) {
        service.updateProduct(name, product);
        return "successfully updated";
    }

}
