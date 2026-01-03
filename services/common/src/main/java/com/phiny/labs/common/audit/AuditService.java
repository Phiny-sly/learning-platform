package com.phiny.labs.common.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    public void log(AuditLog auditLog) {
        // In production, this should write to a database or logging service
        // For now, we'll just log to console
        logger.info("[AUDIT] {} | User: {} ({}) | Action: {} | Resource: {}/{} | IP: {} | Status: {} | Time: {}",
            auditLog.getTimestamp(),
            auditLog.getUserId(),
            auditLog.getUserEmail(),
            auditLog.getAction(),
            auditLog.getResource(),
            auditLog.getResourceId(),
            auditLog.getIpAddress(),
            auditLog.getStatus(),
            auditLog.getTimestamp()
        );
        
        if (auditLog.getErrorMessage() != null) {
            logger.error("[AUDIT ERROR] {}", auditLog.getErrorMessage());
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

