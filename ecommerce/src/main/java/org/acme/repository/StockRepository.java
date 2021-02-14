package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.models.entites.Product;

public class StockRepository implements PanacheRepository<Product> {
}
