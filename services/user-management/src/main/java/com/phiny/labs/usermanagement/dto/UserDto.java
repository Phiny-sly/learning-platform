package com.phiny.labs.usermanagement.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private long tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Collection<? extends GrantedAuthority> grantedAuthorities;
}
