package com.phiny.labs.usermanagement.exception;

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
