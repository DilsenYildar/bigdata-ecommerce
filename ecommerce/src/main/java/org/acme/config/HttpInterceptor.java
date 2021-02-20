package org.acme.config;

import lombok.SneakyThrows;
import org.acme.crypto.Cryptographer;
import org.acme.db.RedisClient;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.acme.db.RedisClient.TOKEN_KEY_PREFIX;

@Provider
public class HttpInterceptor implements ContainerRequestFilter {

    @Inject
    RedisClient redisClient;

    @Inject
    Cryptographer cryptographer;

    @SneakyThrows
    @Override
    public void filter(ContainerRequestContext context) {
        String path = context.getUriInfo().getPath();
        if (path.startsWith("/order") || path.startsWith("/product-buyers") || path.startsWith("/shopping-cart")) {
            String errorMessage = checkAuthentication(context);
            if (!errorMessage.isEmpty()) {
                context.abortWith(Response.status(UNAUTHORIZED).entity("Forbidden!").build());
            }
        }
    }

    private String checkAuthentication(ContainerRequestContext context) throws Exception {
        String[] credentials = getCredentials(context);
        if (credentials.length == 2) {
            String userPass = redisClient.get(TOKEN_KEY_PREFIX.concat(credentials[0]));
            if (userPass != null && !userPass.isEmpty()) {
                String decryptedPass = cryptographer.decrypt(userPass);
                if (!credentials[1].equals(decryptedPass)) {
                    return "invalid cred";
                }
            } else {
                return "invalid cred";
            }
        } else {
            return "invalid cred";
        }
        return "";
    }

    String[] getCredentials(ContainerRequestContext context) {
        String authorization = context.getHeaderString(HttpHeaders.AUTHORIZATION);
        String[] loginInfo = new String[0];
        if (authorization != null && !authorization.isEmpty() && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            loginInfo = credentials.split(":", 2);
        }
        return loginInfo;
    }
}