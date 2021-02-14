package org.acme.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import org.acme.models.Item;
import org.acme.models.Order;
import org.bson.BsonValue;
import org.bson.Document;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public BsonValue add(Order order){
        BsonValue insertedId = null;
        try {
            List<Item> items = order.getItems();
            List<Document> itemList = items.stream().map(
                    item -> new Document().append("name", item.getName()).append("price", item.getPrice())).collect(
                    Collectors.toList());
            InsertManyResult insertedItems = getCollection().insertMany(itemList);
            Document document = new Document()
                    .append("name", order.getUsername())
                    .append("description", new Date().toString())
                    .append("items", insertedItems.getInsertedIds());
            InsertOneResult insertOneResult = getCollection().insertOne(document);
            insertedId = insertOneResult.getInsertedId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertedId;
    }

    private MongoCollection<Document> getCollection(){
        return defaultMongoClient.getDatabase("order").getCollection("order");
    }

}