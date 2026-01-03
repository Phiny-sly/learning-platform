package com.phiny.labs.common.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends BaseException {
    
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED");
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_FAILED", cause);
    }
}

