package com.phiny.labs.tenantservice.exception;

public class InternalServerError extends RuntimeException {
    public InternalServerError() {
        super("Something went wrong");
    }

    public InternalServerError(String message) {
        super(message);
    }
}
