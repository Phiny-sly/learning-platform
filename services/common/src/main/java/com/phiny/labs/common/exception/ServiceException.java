package com.phiny.labs.common.exception;

/**
 * Exception thrown when an internal service operation fails.
 */
public class ServiceException extends BaseException {
    
    public ServiceException(String message) {
        super(message, "SERVICE_ERROR");
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, "SERVICE_ERROR", cause);
    }
    
    public ServiceException(String serviceName, String operation, Throwable cause) {
        super(String.format("Service '%s' failed during operation '%s': %s", 
              serviceName, operation, cause.getMessage()),
              "SERVICE_ERROR", cause);
    }
}

