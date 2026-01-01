package com.phiny.labs.common.audit;

import org.springframework.stereotype.Service;

@Service
public class AuditService {
    
    public void log(AuditLog auditLog) {
        // In production, this should write to a database or logging service
        // For now, we'll just log to console
        System.out.println(String.format(
            "[AUDIT] %s | User: %s (%s) | Action: %s | Resource: %s/%s | IP: %s | Status: %s | Time: %s",
            auditLog.getTimestamp(),
            auditLog.getUserId(),
            auditLog.getUserEmail(),
            auditLog.getAction(),
            auditLog.getResource(),
            auditLog.getResourceId(),
            auditLog.getIpAddress(),
            auditLog.getStatus(),
            auditLog.getTimestamp()
        ));
        
        if (auditLog.getErrorMessage() != null) {
            System.err.println(String.format("[AUDIT ERROR] %s", auditLog.getErrorMessage()));
        }
    }
    
    public void logSecurityEvent(String userId, String action, String resource, String status, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setResource(resource);
        log.setStatus(status);
        log.setIpAddress(ipAddress);
        log.setTimestamp(java.time.LocalDateTime.now());
        log(log);
    }
}

