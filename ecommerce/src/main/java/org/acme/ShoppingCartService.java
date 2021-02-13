package org.acme;

import com.google.gson.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ShoppingCartService {

    @Inject
    RedisClient redisClient;

    String get(String key) {
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

    boolean remove(String key, String name) {
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

    boolean addValueToKey(String key, Item item){
        String userItems = redisClient.get(key);
        JsonArray itemArray = new JsonArray();
        if (userItems != null && !userItems.isEmpty()) {
            itemArray = JsonParser.parseString(userItems).getAsJsonArray();
        }
        itemArray.add(JsonParser.parseString(new Gson().toJson(item)).getAsJsonObject());
        return redisClient.set(key, itemArray.toString(), RedisClient.EXPIRATION_A_DAY);
    }
}
