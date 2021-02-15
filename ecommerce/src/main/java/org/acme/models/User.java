package org.acme.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.types.Node;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class User {
    private Long id;

    @NonNull
    private String username;
    @NonNull
    private String password;

    public static User from(Node node) {
        return new User(node.id(), node.get("username").asString(), node.get("password").asString());
    }
}