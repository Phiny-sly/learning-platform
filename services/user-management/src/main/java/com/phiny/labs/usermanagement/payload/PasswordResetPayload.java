package com.phiny.labs.usermanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetPayload {
    private String code;
    private String newPassword;
    private String email;
}

