package com.phiny.labs.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Autowired(required = false)
    private ServiceTokenGenerator serviceTokenGenerator;

    @Override
    public void apply(RequestTemplate template) {
        // Priority 1: Try to get JWT token from current request context (user token)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Propagate the JWT token from the original request
                template.header("Authorization", authHeader);
                template.header("X-Service-Request", "false"); // User-initiated request
                return;
            }
        }
        
        // Priority 2: Try to get from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            template.header("Authorization", "Bearer " + token);
            template.header("X-Service-Request", "false");
            return;
        }
        
        // Priority 3: Generate service-to-service token
        if (serviceTokenGenerator != null) {
            String serviceToken = serviceTokenGenerator.generateServiceToken();
            template.header("Authorization", "Bearer " + serviceToken);
            template.header("X-Service-Request", "true");
            template.header("X-Source-Service", getServiceName());
        }
    }
    
    private String getServiceName() {
        // Get service name from environment or application properties
        String serviceName = System.getenv("SPRING_APPLICATION_NAME");
        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = System.getProperty("spring.application.name", "unknown-service");
        }
        return serviceName;
    }
}

