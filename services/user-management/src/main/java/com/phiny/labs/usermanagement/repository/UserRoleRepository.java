package com.phiny.labs.usermanagement.repository;

import com.phiny.labs.usermanagement.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    default Optional<UserRole> findByRoleName(String name) {
        return findAll().stream().filter(userRole -> userRole.getRoleName().name().equalsIgnoreCase(name)).findFirst();
    }

}
