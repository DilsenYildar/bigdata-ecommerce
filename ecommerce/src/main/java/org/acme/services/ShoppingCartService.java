package org.acme.services;

import com.google.gson.*;
import org.acme.db.RedisClient;
import org.acme.models.Item;
import org.acme.models.entites.Customer;
import org.acme.models.entites.Product;
import org.acme.repository.CustomerRepository;
import org.acme.repository.ProductRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.acme.db.RedisClient.ITEM_KEY_PREFIX;

@Singleton
public class ShoppingCartService {

    @Inject
    RedisClient redisClient;

    @Inject
    ProductRepository productRepository;

    @Inject
    CustomerRepository customerRepository;

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

    public List<Item> getCartItems(String key) {
        List<Item> result = new ArrayList<>();
        String cart = redisClient.get(key);
        if (cart != null && !cart.isEmpty()) {
            JsonArray cartArray = JsonParser.parseString(cart).getAsJsonArray();
            for (JsonElement cartElem : cartArray) {
                Item item = new Gson().fromJson(cartElem.getAsJsonObject(), Item.class);
                result.add(item);
            }
        }
        return result;
    }

    public void clearCart(String key) {
        redisClient.remove(key);
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
            Customer customer = customerRepository.find("username", username).firstResult();
            if (customer != null) {
                String userItems = redisClient.get(ITEM_KEY_PREFIX.concat(username));
                JsonArray itemArray = new JsonArray();
                if (userItems != null && !userItems.isEmpty()) {
                    itemArray = JsonParser.parseString(userItems).getAsJsonArray();
                }
                Item item = new Item(product.getName(), product.getPrice());
                itemArray.add(JsonParser.parseString(new Gson().toJson(item)).getAsJsonObject());
                success = redisClient.set("item:".concat(username), itemArray.toString(), RedisClient.EXPIRATION_A_DAY);
            }
        }
        return success;
    }

    public void updateCartItems(Product product, String productName) {
        Set<String> keys = redisClient.getKeysWithPattern(ITEM_KEY_PREFIX);
        keys.forEach(key -> {
            String userItems = redisClient.get(key);
            if (userItems != null && !userItems.isEmpty()) {
                JsonArray itemArray = JsonParser.parseString(userItems).getAsJsonArray();
                JsonArray updatedItemArray = itemArray.deepCopy();
                itemArray.forEach(item -> {
                    JsonObject itemJson = item.getAsJsonObject();
                    if (productName.equals(itemJson.get("name").getAsString())) {
                        updatedItemArray.remove(itemJson);
                        if (product.getPrice() != null) {
                            itemJson.addProperty("price", product.getPrice());
                        }
                        if (product.getName() != null) {
                            itemJson.addProperty("name", product.getName());
                        }
                        if (product.getBrand() != null) {
                            itemJson.addProperty("brand", product.getBrand());
                        }
                        updatedItemArray.add(itemJson);
                    }
                });
                redisClient.set(key, updatedItemArray.toString(), RedisClient.EXPIRATION_A_DAY);
            }
        });
    }
}
