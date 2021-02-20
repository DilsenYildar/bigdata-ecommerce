package org.acme.models.entites;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.types.Node;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private String name;

    @Expose
    private String brand;

    @Expose
    private String price;


    public static Product from(Node node) {
        return new Product(node.id(), node.get("name").asString(), node.get("brand").asString(),
                node.get("price").asString());
    }
}
