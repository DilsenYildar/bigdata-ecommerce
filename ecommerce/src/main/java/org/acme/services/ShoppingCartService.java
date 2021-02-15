package org.acme.services;

import com.google.gson.*;
import org.acme.db.RedisClient;
import org.acme.models.Item;
import org.acme.models.entites.Product;
import org.acme.repository.ProductRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShoppingCartService {

    @Inject
    RedisClient redisClient;

    @Inject
    ProductRepository productRepository;

    public String get(String key) {
        String result = null;
        String cart = redisClient.get(key);
        if (cart != null && !cart.isEmpty()) {
            JsonArray resultArray = new JsonArray();
            JsonArray cartArray = JsonParser.parseString(cart).getAsJsonArray();
            for (JsonElement cartElem : cartArray) {
                resultArray.add(cartElem.getAsJsonObject());
            }
            result = resultArray.toString();
        }
        return result;
    }

    public boolean remove(String key, String name) {
        boolean updated = false;
        String cart = redisClient.get(key);
        if (cart != null && !cart.isEmpty()) {
            JsonArray cartArray = JsonParser.parseString(cart).getAsJsonArray();
            JsonArray updatedArray = new JsonArray();
            for (JsonElement cartElem : cartArray) {
                JsonObject cartJson = cartElem.getAsJsonObject();
                if (!name.equals(cartJson.get("name").getAsString())) {
                    updatedArray.add(cartJson);
                }
            }
            updated = redisClient.set(key, updatedArray.toString(), RedisClient.EXPIRATION_A_DAY);
        }
        return updated;
    }

    public boolean addCartItem(String username, String productName) {
        boolean success = false;
        Product product = productRepository.findByName(productName);
        if (product != null) {
            String userItems = redisClient.get(username);
            JsonArray itemArray = new JsonArray();
            if (userItems != null && !userItems.isEmpty()) {
                itemArray = JsonParser.parseString(userItems).getAsJsonArray();
            }
            Item item = new Item(product.getName(), product.getPrice());
            itemArray.add(JsonParser.parseString(new Gson().toJson(item)).getAsJsonObject());
            success = redisClient.set(username, itemArray.toString(), RedisClient.EXPIRATION_A_DAY);
        }
        return success;
    }
}
