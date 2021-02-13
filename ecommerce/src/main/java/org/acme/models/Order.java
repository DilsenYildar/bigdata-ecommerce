package org.acme.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Order {
    private String username;
    private String creationDate;
    private List<Item> items;
}
