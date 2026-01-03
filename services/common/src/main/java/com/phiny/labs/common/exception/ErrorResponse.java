package com.phiny.labs.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard error response structure for API error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String errorCode;
    private final String path;
    
    public ErrorResponse(int status, String error, String message, String errorCode, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getError() {
        return error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getPath() {
        return path;
    }
}

