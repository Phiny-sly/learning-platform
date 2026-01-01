package com.phiny.labs.common.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal implements UserDetails {
    private final String email;
    private final Long userId;
    private final Long tenantId;
    private final String role;

    public UserPrincipal(String email, Long userId, Long tenantId, String role) {
        this.email = email;
        this.userId = userId;
        this.tenantId = tenantId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null; // Not needed for JWT authentication
    }

    @Override
    public Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return java.util.Collections.singletonList(
            new org.springframework.security.core.authority.SimpleGrantedAuthority(role)
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

