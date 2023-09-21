package com.turntabl.labs.usermanagement.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private long tenantId;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

}
