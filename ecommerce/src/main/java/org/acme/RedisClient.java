package org.acme;

import redis.clients.jedis.Jedis;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
class RedisClient {
    Jedis jedis = new Jedis("localhost", 6379);

    public String get(String key) {
        return jedis.get(key);
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public void incrBy(String key, Long incrementBy) {
        jedis.incrBy(key, incrementBy);
    }
}