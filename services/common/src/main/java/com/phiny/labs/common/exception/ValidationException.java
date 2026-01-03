package com.phiny.labs.common.exception;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends BaseException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }
    
    public ValidationException(String field, String reason) {
        super(String.format("Validation failed for field '%s': %s", field, reason),
              "VALIDATION_ERROR");
    }
}

