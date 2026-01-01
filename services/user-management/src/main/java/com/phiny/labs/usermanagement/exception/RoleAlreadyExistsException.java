package com.phiny.labs.usermanagement.exception;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException(String role) {
        super(String.format("User already has %s role", role));
    }
}
