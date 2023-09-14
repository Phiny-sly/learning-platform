package com.turntabl.labs.usermanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPayload {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String password;
    private long tenantId;
}
