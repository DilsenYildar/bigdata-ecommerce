package org.acme;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserInfo {
    private String username;
    private String password;
}