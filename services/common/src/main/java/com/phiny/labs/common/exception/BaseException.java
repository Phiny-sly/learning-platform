package com.phiny.labs.common.exception;

/**
 * Base exception class for all custom exceptions in the application.
 * Provides a consistent structure for error handling.
 */
public abstract class BaseException extends RuntimeException {
    
    private final String errorCode;
    
    protected BaseException(String message) {
        super(message);
        this.errorCode = null;
    }
    
    protected BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    
    protected BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

