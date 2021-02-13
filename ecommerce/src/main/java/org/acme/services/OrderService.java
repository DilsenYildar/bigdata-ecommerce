package org.acme.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.acme.models.Item;
import org.acme.models.Order;
import org.bson.Document;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class OrderService {

    @Inject
    MongoClient defaultMongoClient;

    public List<Order> list(){
        List<Order> list = new ArrayList<>();

        try (var cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String creationDate = document.getString("creationDate");
                Order order = new Order(document.getString("username"), creationDate,
                        document.getList("items", Item.class));
                list.add(order);
            }
        }
        return list;
    }

    public void add(Order order){
        try {
            Document document = new Document()
                    .append("name", order.getUsername())
                    .append("description", new Date().toString())
                    .append("items", order.getItems());
            getCollection().insertOne(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MongoCollection<Document> getCollection(){
        return defaultMongoClient.getDatabase("order").getCollection("order");
    }

}