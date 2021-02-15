package org.acme.models.entites;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.types.Node;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private Long id;

    private String name;

    private String brand;

    private String price;


    public static Product from(Node node) {
        return new Product(node.id(), node.get("name").asString(), node.get("brand").asString(),
                node.get("price").asString());
    }
}
