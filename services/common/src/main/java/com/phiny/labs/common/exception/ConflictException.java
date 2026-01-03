package com.phiny.labs.common.exception;

/**
 * Exception thrown when a resource conflict occurs (e.g., duplicate entry).
 */
public class ConflictException extends BaseException {
    
    public ConflictException(String message) {
        super(message, "RESOURCE_CONFLICT");
    }
    
    public ConflictException(String resourceName, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", resourceName, field, value),
              "RESOURCE_CONFLICT");
    }
}

