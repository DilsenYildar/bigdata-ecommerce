package org.acme.services;

import org.acme.models.ProductBuyers;
import org.acme.models.entites.Customer;
import org.acme.models.entites.Product;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.async.ResultCursor;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.CompletionStage;

@Singleton
public class ProductBuyersService {

    @Inject
    Driver driver;

    public CompletionStage<Response> createProductToNeo4j(Product product) {
        boolean existingRecord = driver.session().readTransaction(tx -> {
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
    }

    public CompletionStage<Response> createUserToNeo4j(Customer customer) {
        boolean existingRecord = driver.session().readTransaction(tx -> {
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
    }

    public CompletionStage<Response> createRelationWithProductAndCustomer(ProductBuyers productBuyers) {
        boolean existingRecord = driver.session().readTransaction(tx -> {
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
    }

}
