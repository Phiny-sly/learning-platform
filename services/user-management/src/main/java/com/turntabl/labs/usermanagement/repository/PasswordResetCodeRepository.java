package com.turntabl.labs.usermanagement.repository;

import com.turntabl.labs.usermanagement.entity.PasswordResetCode;
import com.turntabl.labs.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, String> {
    Optional<PasswordResetCode> findByUser(User user);
    Optional<PasswordResetCode> findByIdAndUser(String code, User user);
}

