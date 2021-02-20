package org.acme.services;

import org.acme.models.entites.Product;
import org.acme.repository.ProductRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class ProductService {

    @Inject
    ProductRepository productRepository;

    public Product get(String pName) throws Exception {
        return productRepository.find("name", pName).firstResult();
    }

    @Transactional
    public void set(Product product) {
        productRepository.persist(product);
    }

    @Transactional
    public boolean remove(String pName) {
        return productRepository.delete("name", pName) != 0;
    }
}