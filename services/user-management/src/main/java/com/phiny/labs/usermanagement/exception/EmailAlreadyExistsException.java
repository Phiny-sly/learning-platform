package com.phiny.labs.usermanagement.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(String.format("User with email %s already exists", message));
    }
}
