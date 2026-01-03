package com.phiny.labs.common.exception;

/**
 * Exception thrown when communication with an external service fails.
 */
public class ExternalServiceException extends BaseException {
    
    private final String serviceName;
    
    public ExternalServiceException(String serviceName, String message) {
        super(String.format("External service '%s' error: %s", serviceName, message),
              "EXTERNAL_SERVICE_ERROR");
        this.serviceName = serviceName;
    }
    
    public ExternalServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("External service '%s' error: %s", serviceName, message),
              "EXTERNAL_SERVICE_ERROR", cause);
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
}

