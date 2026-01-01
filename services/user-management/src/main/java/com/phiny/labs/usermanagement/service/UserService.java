package com.phiny.labs.usermanagement.service;

import com.phiny.labs.usermanagement.dto.TokenResponse;
import com.phiny.labs.usermanagement.dto.UserDto;
import com.phiny.labs.usermanagement.payload.LoginPayload;
import com.phiny.labs.usermanagement.payload.PasswordResetPayload;
import com.phiny.labs.usermanagement.payload.PasswordResetRequestPayload;
import com.phiny.labs.usermanagement.payload.UserPayload;
import com.phiny.labs.usermanagement.payload.UserProfilePayload;
import com.phiny.labs.usermanagement.payload.UserRolePayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserPayload userDto);

    UserDto updateUserDetails(long userId, UserProfilePayload userDto);

    UserDto getUserById(long userId);

    UserDto updateUserRole(UserRolePayload userDto);

    String generateToken(LoginPayload payload);
    
    TokenResponse login(LoginPayload payload);
    
    TokenResponse refreshToken(String refreshToken);

    UserDto getUserByEmail(String email);

    Page<UserDto> getAllUsers(Pageable pageable);

    void requestPasswordReset(PasswordResetRequestPayload payload);

    void resetPassword(PasswordResetPayload payload);

    void deleteUser(long userId);
}
