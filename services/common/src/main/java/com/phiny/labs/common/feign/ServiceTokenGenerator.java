package com.phiny.labs.common.feign;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ServiceTokenGenerator {

    @Value("${jwt.secret:eyJhbGciOiJIUzI1NiIsInR5cCI}")
    private String secret;
    
    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateServiceToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("service", serviceName);
        claims.put("type", "SERVICE");
        claims.put("scope", "INTER_SERVICE");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(serviceName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(getSignKey())
                .compact();
    }
}

