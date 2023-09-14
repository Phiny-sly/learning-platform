package com.turntabl.labs.usermanagement.service;

import com.turntabl.labs.usermanagement.dto.UserDto;
import com.turntabl.labs.usermanagement.payload.UserPayload;
import com.turntabl.labs.usermanagement.payload.UserProfilePayload;
import com.turntabl.labs.usermanagement.payload.UserRolePayload;

public interface UserService {
    UserDto createUser(UserPayload userDto);

    UserDto updateUserDetails(long userId, UserProfilePayload userDto);

    UserDto getUserById(long userId);

    UserDto updateUserRole(UserRolePayload userDto);

}
