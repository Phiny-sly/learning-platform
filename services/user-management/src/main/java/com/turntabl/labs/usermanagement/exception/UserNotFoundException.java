package com.turntabl.labs.usermanagement.exception;

import com.turntabl.labs.usermanagement.payload.LoginPayload;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("User with id %d not found", id));
    }

    public UserNotFoundException(String email) {
        super(String.format("User with email %s not found", email));
    }

    public UserNotFoundException() {
        super("Email or Password is incorrect");
    }
}
