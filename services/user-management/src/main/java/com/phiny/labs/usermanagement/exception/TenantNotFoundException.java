package com.phiny.labs.usermanagement.exception;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(long id) {
        super(String.format("Tenant with id %d not found", id));
    }
}
