package com.phiny.labs.common.exception;

/**
 * Exception thrown when token operations fail (JWT, refresh tokens, etc.).
 */
public class TokenException extends BaseException {
    
    public TokenException(String message) {
        super(message, "TOKEN_ERROR");
    }
    
    public TokenException(String message, Throwable cause) {
        super(message, "TOKEN_ERROR", cause);
    }
    
    public static TokenException expired(String tokenType) {
        return new TokenException(String.format("%s token has expired", tokenType));
    }
    
    public static TokenException invalid(String tokenType) {
        return new TokenException(String.format("Invalid %s token", tokenType));
    }
    
    public static TokenException revoked(String tokenType) {
        return new TokenException(String.format("%s token has been revoked", tokenType));
    }
}

