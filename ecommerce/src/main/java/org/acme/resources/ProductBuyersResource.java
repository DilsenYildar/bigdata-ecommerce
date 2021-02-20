package org.acme.resources;

import org.acme.models.entites.Customer;
import org.acme.models.ProductBuyers;
import org.acme.models.entites.Product;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.async.ResultCursor;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.CompletionStage;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/product-buyers")
public class ProductBuyersResource {

    @Inject
    Driver driver;

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
        Session session = driver.session(); // marka yarat. -> kullanıcı yarat. && markaya bu kullanıcı ile ilişki tanımla
        boolean existingRecord = session.readTransaction(tx -> {
            // bu marka var mı?
            Result result = tx.run("MATCH (pb:Product) WHERE pb.name = $name RETURN pb",
                    Values.parameters("name", product.getName()));
            return result.list().isEmpty();
        });
        AsyncSession asyncSession = driver.asyncSession();
        return asyncSession.writeTransactionAsync(tx -> {
            if (existingRecord) {
                return tx.runAsync("CREATE (pb:Product {name: $name, brand: $brand, price: $price}) RETURN pb",
                        Values.parameters("name", product.getName(), "brand", product.getBrand(), "price", product.getPrice())
                ).thenCompose(ResultCursor::singleAsync);
            } else {
                return null;
            }
        }).thenApply(record -> record != null ? Product.from(record.get("pb").asNode()) : null).thenCompose(
                persistedProduct -> asyncSession.closeAsync().thenApply(signal -> persistedProduct)).thenApply(
                persistedProduct -> Response.created(URI.create("/products/" +
                        (persistedProduct != null ? persistedProduct.getId() : ""))).build());
    } // todo: use this api when product created.

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

        Session session = driver.session();
        boolean existingRecord = session.readTransaction(tx -> {
            // bu kullanıcı ile kayıt var mı?
            Result result = tx.run("MATCH(u:Customer) WHERE u.username = $username RETURN u",
                    Values.parameters("username", customer.getUsername()));
            return result.list().isEmpty();
        });
        AsyncSession asyncSession = driver.asyncSession();
        return asyncSession.writeTransactionAsync(tx -> {
            if (existingRecord) {
                return tx.runAsync("CREATE (u:Customer {username: $username}) RETURN u",
                        Values.parameters("username", customer.getUsername())).thenCompose(ResultCursor::singleAsync);
            } else {
                return null;
            }
        }).thenApply(record -> record != null ? Product.from(record.get("pb").asNode()) : null).thenCompose(
                persistedUser -> asyncSession.closeAsync().thenApply(signal -> persistedUser)).thenApply(
                persistedUser -> Response.created(URI.create("/products/" +
                        (persistedUser != null ? persistedUser.getId() : ""))).build());
    } // todo: use this api when user created.

    @POST
    @Path("/user/assign")
    public CompletionStage<Response> assignProductToUser(ProductBuyers productBuyers) {
        Session session = driver.session();
        boolean existingRecord = session.readTransaction(tx -> {
            Result result = tx.run("MATCH (u:Customer)-[:owns] -> (p:Product) WHERE u.username = $username AND p.name = $name RETURN p",
                    Values.parameters("username", productBuyers.getUsername(), "name", productBuyers.getProductName()));
            return result.list().isEmpty();
        });
        AsyncSession asyncSession = driver.asyncSession();
        return asyncSession.writeTransactionAsync(tx -> {
            if (existingRecord) {
                return tx.runAsync("MATCH (p:Product), (u:Customer) WHERE p.name = $name AND u.username = $username" +
                                " CREATE (u)-[r:owns]->(p) RETURN type(r)",
                        Values.parameters("name", productBuyers.getProductName(), "username",
                                productBuyers.getUsername())).thenCompose(ResultCursor::singleAsync);
            } else {
                return null;
            }
        }).thenApply(record -> record != null ? Product.from(record.get("pb").asNode()) : null).thenCompose(
                persistedUser -> asyncSession.closeAsync().thenApply(signal -> persistedUser)).thenApply(
                persistedUser -> Response.created(URI.create("/products/" +
                        (persistedUser != null ? persistedUser.getId() : ""))).build());
    } // todo: use this api when user completed the order

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
