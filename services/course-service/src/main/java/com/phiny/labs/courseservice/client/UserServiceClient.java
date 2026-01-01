package com.phiny.labs.courseservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management", path = "/api/users")
public interface UserServiceClient {

    @GetMapping("/details/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @GetMapping("/email/{email}")
    UserDto getUserByEmail(@PathVariable("email") String email);

    class UserDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private Long tenantId;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Long getTenantId() { return tenantId; }
        public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    }
}

