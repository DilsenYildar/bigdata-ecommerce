package org.acme;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class IncrementService {

    @Inject
    RedisClient redisClient;


    String get(String key) {
        return redisClient.get(key);
    }

    void set(String key, String value) {
        redisClient.set(key, value);
    }



}