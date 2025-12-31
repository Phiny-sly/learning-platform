package com.turntabl.labs.usermanagement.service;

import com.turntabl.labs.usermanagement.dto.UserDto;
import com.turntabl.labs.usermanagement.payload.LoginPayload;
import com.turntabl.labs.usermanagement.payload.PasswordResetPayload;
import com.turntabl.labs.usermanagement.payload.PasswordResetRequestPayload;
import com.turntabl.labs.usermanagement.payload.UserPayload;
import com.turntabl.labs.usermanagement.payload.UserProfilePayload;
import com.turntabl.labs.usermanagement.payload.UserRolePayload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserPayload userDto);

    UserDto updateUserDetails(long userId, UserProfilePayload userDto);

    UserDto getUserById(long userId);

    UserDto updateUserRole(UserRolePayload userDto);

    String generateToken(LoginPayload payload);

    UserDto getUserByEmail(String email);

    Page<UserDto> getAllUsers(Pageable pageable);

    void requestPasswordReset(PasswordResetRequestPayload payload);

    void resetPassword(PasswordResetPayload payload);

    void deleteUser(long userId);
}
