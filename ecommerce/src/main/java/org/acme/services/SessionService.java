package org.acme.services;


import org.acme.crypto.Cryptographer;
import org.acme.db.RedisClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionService {

    @Inject
    RedisClient redisClient;

    @Inject
    Cryptographer cryptographer;

    public String decryptPassword(String encryptedPass) throws Exception {
        return cryptographer.decrypt(encryptedPass);
    }

    public boolean set(String username, String password) {
        return redisClient.set(username, password, 0);
    }

    public boolean remove(String username) {
        return redisClient.remove(username);
    }
}