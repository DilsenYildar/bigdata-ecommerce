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

    @Inject
    ShoppingCartService shoppingCartService;

    public Product get(String pName) throws Exception {
        return productRepository.find("name", pName).firstResult();
    }

    @Transactional
    public void create(Product product) {
        productRepository.persist(product);
    }

    @Transactional
    public boolean remove(String pName) {
        return productRepository.delete("name", pName) != 0;
    }

    @Transactional
    public void updateProduct(String name, Product requestedProduct) {
        Product actualProduct = productRepository.find("name", name).firstResult();
        if (requestedProduct.getName() != null) {
            actualProduct.setName(requestedProduct.getName());
        }
        if (requestedProduct.getPrice() != null) {
            actualProduct.setPrice(requestedProduct.getPrice());
        }
        if (requestedProduct.getBrand() != null) {
            actualProduct.setBrand(requestedProduct.getBrand());
        }
        if (requestedProduct.getQuantity() != null) {
            actualProduct.setQuantity(requestedProduct.getQuantity());
        }
        shoppingCartService.updateCartItems(requestedProduct, name);
        productRepository.persist(actualProduct);
    }

}