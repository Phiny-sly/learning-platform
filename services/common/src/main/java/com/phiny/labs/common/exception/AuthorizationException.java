package com.phiny.labs.common.exception;

/**
 * Exception thrown when authorization fails (user lacks required permissions).
 */
public class AuthorizationException extends BaseException {
    
    public AuthorizationException(String message) {
        super(message, "AUTHORIZATION_FAILED");
    }
    
    public AuthorizationException(String resource, String action) {
        super(String.format("User is not authorized to %s %s", action, resource),
              "AUTHORIZATION_FAILED");
    }
}

