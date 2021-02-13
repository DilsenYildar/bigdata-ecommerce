package org.acme;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class SessionService {

    @Inject
    RedisClient redisClient;

    @Inject
    Cryptographer cryptographer;

    String get(String key) throws Exception {
        String decryptedPass = null;
        String value = redisClient.get(key);
        if (value != null) {
            decryptedPass = cryptographer.decrypt(value);
        }
        return decryptedPass;
    }

    boolean set(String key, String value) {
        return redisClient.set(key, cryptographer.encrypt(value), RedisClient.EXPIRATION_IN_THREE_MINUTES);
    }

    boolean remove(String key) {
        return redisClient.remove(key);
    }
}