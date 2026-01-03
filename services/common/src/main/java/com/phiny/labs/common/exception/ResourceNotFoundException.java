package com.phiny.labs.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends BaseException {
    
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s not found with identifier: %s", resourceName, identifier),
              "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "RESOURCE_NOT_FOUND", cause);
    }
}

