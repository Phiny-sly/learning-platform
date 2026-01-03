package com.phiny.labs.common.exception;

/**
 * Exception thrown when notification operations fail.
 */
public class NotificationException extends BaseException {
    
    public NotificationException(String message) {
        super(message, "NOTIFICATION_ERROR");
    }
    
    public NotificationException(String message, Throwable cause) {
        super(message, "NOTIFICATION_ERROR", cause);
    }
    
    public NotificationException(String notificationType, String reason) {
        super(String.format("Failed to send %s notification: %s", notificationType, reason),
              "NOTIFICATION_ERROR");
    }
}

