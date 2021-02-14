package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.models.entites.Product;

public class ProductRepository implements PanacheRepository<Product> {


}
