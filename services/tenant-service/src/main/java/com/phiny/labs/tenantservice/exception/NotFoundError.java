package com.phiny.labs.tenantservice.exception;

import java.util.UUID;

public class NotFoundError extends RuntimeException{
    public NotFoundError() {
        super("Tenant not found");
    }

    public NotFoundError(String message) {
        super(message);
    }

    public NotFoundError(UUID id) {
        super("Tenant with Id "+id.toString()+" not found");
    }
}
