package org.acme.services;

import org.acme.models.Order;
import org.acme.repository.OrderRepository;
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
    ShoppingCartService shoppingCartService;

    @Transactional
    public void saveOrder(Order order) {
        order.setItems(shoppingCartService.getCartItems(ITEM_KEY_PREFIX.concat(order.getUsername())));
        order.setCreationDate(LocalDate.now().toString());
        orderRepository.persist(order);
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