package org.acme;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Item {
    private String username;
    private String name;
    private String price;
}
