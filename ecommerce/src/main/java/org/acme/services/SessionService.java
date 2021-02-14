package org.acme.services;


import org.acme.crypto.Cryptographer;
import org.acme.db.RedisClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.acme.db.RedisClient.EXPIRATION_IN_THREE_MINUTES;

@Singleton
public class SessionService {

    @Inject
    RedisClient redisClient;

    @Inject
    Cryptographer cryptographer;

    public String get(String username) throws Exception {
        String decryptedPass = null;
        String value = redisClient.get(username);
        if (value != null) {
            decryptedPass = cryptographer.decrypt(value);
        }
        return decryptedPass;
    }

    public boolean set(String username, String password) {
        return redisClient.set(username, cryptographer.encrypt(password), EXPIRATION_IN_THREE_MINUTES);
    }

    public boolean remove(String username) {
        return redisClient.remove(username);
    }
}