package org.acme.services;

import org.acme.models.Item;
import org.acme.models.Order;
import org.acme.models.ProductBuyers;
import org.acme.repository.OrderRepository;
import org.acme.repository.ProductRepository;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.acme.db.RedisClient.ITEM_KEY_PREFIX;

@Singleton
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    ProductRepository productRepository;

    @Inject
    ShoppingCartService shoppingCartService;

    @Inject
    ProductBuyersService productBuyersService;

    @Transactional
    public void saveOrder(Order order) {
        order.setItems(shoppingCartService.getCartItems(ITEM_KEY_PREFIX.concat(order.getUsername())));
        order.setCreationDate(LocalDate.now().toString());
        orderRepository.persist(order);
        shoppingCartService.clearCart(ITEM_KEY_PREFIX.concat(order.getUsername()));
        updateStock(order);
        assignUserToProductOnNeo4j(order);
    }

    private void assignUserToProductOnNeo4j(Order order) {
        for (Item item : order.getItems()) {
            productBuyersService.createRelationWithProductAndCustomer(new ProductBuyers(order.getUsername(), item.getName()));
        }
    }

    private void updateStock(Order order) {
        for (Item item : order.getItems()) {
            productRepository.update("UPDATE Product As p SET p.quantity = coalesce(p.quantity, 0) -1 WHERE p.name=?1",
                    item.getName());
        }
    }

    public List<Order> getOrders() {
        return orderRepository.findAll().list();
    }

    @Transactional
    public boolean deleteOrder(String id) {
        return orderRepository.deleteById(new ObjectId(id));
    }

    @Transactional
    public void updateOrder(Order order) {
        orderRepository.update(order);
    }

    @Transactional
    public void deleteOrders() {
        orderRepository.deleteAll();
    }
}