package org.acme.models.entites;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.types.Node;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Expose
    @Column(unique = true)
    private String name;

    @Expose
    private String brand;

    @Expose
    private String price;

    @Expose
    private Long quantity;

    public static Product from(Node node) {
        return new Product(node.id(), node.get("name").asString(), node.get("brand").asString(),
                node.get("price").asString(), node.get("quantity").asLong());
    }
}
