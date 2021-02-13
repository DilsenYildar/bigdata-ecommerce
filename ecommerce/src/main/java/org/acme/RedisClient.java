package org.acme;

import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
class RedisClient {
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;

//    public void incrBy(String key, Long incrementBy) {
//        jedis.incrBy(key, incrementBy);
//    }

    public static final int EXPIRATION_A_DAY = 24 * 60 * 60;

    public static final int EXPIRATION_IN_THREE_MINUTES = 3 * 60;

    private JedisPool readPool;

    public void closePool() {
        if (readPool != null) {
            readPool.close();
        }
    }

    public void deleteKeys(String pattern) {
        Set<String> matchingKeys = new HashSet<>();
        ScanParams params = new ScanParams().match(pattern);
        try (Jedis conn = getConnection()) {
            String cursor = "0";
            do {
                ScanResult<String> scanResult = conn.scan(cursor, params);
                matchingKeys.addAll(scanResult.getResult());
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));

            if (matchingKeys.size() != 0) {
                conn.del(matchingKeys.toArray(new String[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteKeys(List<String> keys) {
        try (Jedis conn = getConnection()) {
            conn.del(keys.toArray(new String[0]));
        }
    }

    public void flushDB() {
        try (Jedis conn = getConnection()) {
            conn.flushDB();
        }
    }

    public boolean remove(String key) {
        boolean success = false;
        try (Jedis conn = getConnection()) {
            conn.del(key);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean set(String key, String value, int expiration) {
        boolean isSuccess = false;
        try (Jedis conn = getConnection()) {
            conn.set(key, value, new SetParams().ex(expiration != 0 ? expiration : EXPIRATION_A_DAY));
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public String get(String key) {
        long startTime = System.currentTimeMillis();
        String value = null;
        try (Jedis conn = getConnection()) {
            value = conn.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long took = System.currentTimeMillis() - startTime;
            if (took >= 1000) {
                System.err.printf("retrieving redis value with key:%s took: %s%n", key, took);
            }
        }
        return value;
    }

    public Set<String> getKeysWithPattern(String pattern) {
        Set<String> keys = new HashSet<>();
        long startTime = System.currentTimeMillis();
        try (Jedis conn = getConnection()) {
            keys = conn.keys("*".concat(pattern).concat("*"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long took = System.currentTimeMillis() - startTime;
            if (took >= 1000) {
                System.err.printf("retrieving redis value with took: %s%n", took);
            }
        }
        return keys;
    }

    public Set<String> getKeys() {
        Set<String> keys = new HashSet<>();
        long startTime = System.currentTimeMillis();
        String value = null;
        try (Jedis conn = getConnection()) {
            keys = conn.keys("*");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long took = System.currentTimeMillis() - startTime;
            if (took >= 1000) {
                System.err.printf("retrieving redis value with took: %s%n", took);
            }
        }
        return keys;
    }

    private Jedis getConnection() {
        Jedis resource;
        try {
            resource = getPool(REDIS_HOST, REDIS_PORT).getResource();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return resource;
    }

    private JedisPool getPool(String host, int port) {
        if (readPool == null) {
            readPool = getJedisPool(host, port);
        }
        return readPool;
    }

    public JedisPool getJedisPool(String host, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        config.setMaxIdle(128);
        config.setBlockWhenExhausted(true);
        config.setMaxWaitMillis(15000);
        return new JedisPool(config, host, port, 15000);
    }
}