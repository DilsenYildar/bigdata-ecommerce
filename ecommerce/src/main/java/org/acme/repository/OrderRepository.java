package org.acme.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.acme.models.Order;

import javax.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class OrderRepository implements PanacheMongoRepository<Order> {
}