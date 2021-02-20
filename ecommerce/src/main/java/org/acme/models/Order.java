package org.acme.models;

import com.google.gson.annotations.Expose;
import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@MongoEntity(collection = "Order")
@NoArgsConstructor
public class Order extends PanacheMongoEntity {
    @BsonProperty("creation_date")
    private String creationDate;
    @Expose
    private String username;
    @Expose
    private List<Item> items;
}
