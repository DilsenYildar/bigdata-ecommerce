package org.acme.models.entites;

import com.google.gson.annotations.Expose;
import lombok.*;
import org.neo4j.driver.types.Node;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Expose
    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    @Expose
    private String email;

    public Customer(long id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static Customer from(Node node) {
        return new Customer(node.id(), node.get("username").asString(), node.get("password").asString(),
                node.get("email").asString());
    }
}