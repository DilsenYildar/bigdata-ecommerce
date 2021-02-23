package org.acme.resources;

import org.acme.models.entites.Customer;
import org.acme.models.ProductBuyers;
import org.acme.models.entites.Product;
import org.acme.services.ProductBuyersService;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.async.ResultCursor;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/product-buyers")
public class ProductBuyersResource {

    @Inject
    Driver driver;

    @Inject
    ProductBuyersService productBuyersService;

    @GET
    public CompletionStage<Response> getProducts() {
        AsyncSession session = driver.asyncSession();
        return session
                .runAsync("MATCH (pb:Product) RETURN pb ORDER BY pb.id")
                .thenCompose(cursor ->
                        cursor.listAsync(record -> Product.from(record.get("pb").asNode()))
                )
                .thenCompose(productBuyers ->
                        session.closeAsync().thenApply(signal -> productBuyers)
                )
                .thenApply(Response::ok)
                .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    public CompletionStage<Response> createProduct(Product product) {
        // marka yarat. -> kullanıcı yarat. && markaya bu kullanıcı ile ilişki tanımla
        return productBuyersService.createProductToNeo4j(product);
    }

    @GET
    @Path("/{id}") // todo: fix empty response.
    public CompletionStage<Response> getByProductId(@PathParam("id") String id) {
        AsyncSession session = driver.asyncSession();
        return session.runAsync("MATCH (pb:Product) WHERE id(pb) = $id RETURN pb"
                        , Values.parameters("id", id))
                .thenCompose(cursor ->
                        cursor.listAsync(record -> Product.from(record.get("pb").asNode()))
                )
                .thenCompose(product ->
                        session.closeAsync().thenApply(signal -> product)
                )
                .thenApply(Response::ok)
                .thenApply(Response.ResponseBuilder::build);
    }

    @DELETE
    @Path("{id}")
    public CompletionStage<Response> deleteProduct(@PathParam("id") Long id) {
        AsyncSession session = driver.asyncSession();
        return session
                .writeTransactionAsync(tx -> tx
                        .runAsync("MATCH (pb:Product) WHERE id(pb) = $id DETACH DELETE pb", Values.parameters("id", id))
                        .thenCompose(ResultCursor::consumeAsync)
                )
                .thenCompose(response -> session.closeAsync())
                .thenApply(signal -> Response.noContent().build());
    }

    @POST
    @Path("/user")
    public CompletionStage<Response> createUser(Customer customer) {
        // bu marka ile kullanıcı var mı
        // "MATCH (pb:Product)-[:owns] -> (u:Customer) WHERE u.username = $username AND pb.name = $name RETURN pb",

//        "CREATE (pb:Product {name: $name, brand: $brand, price: $price}) -[:owns]-> (u:Customer " +
//                "{username: $username}) RETURN pb, u",
        return productBuyersService.createUserToNeo4j(customer);
    }

    @POST
    @Path("/user/assign")
    public CompletionStage<Response> assignProductToUser(ProductBuyers productBuyers) {
        return productBuyersService.createRelationWithProductAndCustomer(productBuyers);
    }

    @POST
    @Path("/users")
    public CompletionStage<Response> getProductUsers(ProductBuyers productBuyers) {
        AsyncSession session = driver.asyncSession();
        return session
                .runAsync("MATCH (u:Customer)-[:owns]->(p:Product) WHERE p.name = $name RETURN u",
                        Values.parameters("name", productBuyers.getProductName()))
                .thenCompose(cursor ->
                        cursor.listAsync(record -> Customer.from(record.get("u").asNode()))
                )
                .thenCompose(users ->
                        session.closeAsync().thenApply(signal -> users)
                )
                .thenApply(Response::ok)
                .thenApply(Response.ResponseBuilder::build);
    }
}
