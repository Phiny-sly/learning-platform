package com.turntabl.labs.usermanagement.config;

import com.turntabl.labs.usermanagement.payload.UserPayload;
import com.turntabl.labs.usermanagement.dto.UserDto;
import com.turntabl.labs.usermanagement.entity.User;
import com.turntabl.labs.usermanagement.entity.UserProfile;
import com.turntabl.labs.usermanagement.payload.UserProfilePayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    @Mapping(target = "firstName", expression = "java(user.getUserProfile().getFirstName())")
    @Mapping(target = "lastName", expression = "java(user.getUserProfile().getLastName())")
    @Mapping(target = "avatarUrl", expression = "java(user.getUserProfile().getAvatarUrl())")
    @Mapping(target = "phoneNumber", expression = "java(user.getUserProfile().getPhoneNumber())")
    @Mapping(target = "email", expression = "java(user.getUserProfile().getEmail())")
    @Mapping(target = "role", expression = "java(user.getUserRole().getRoleName().name())")
    @Mapping(target = "tenantId", source = "tenantId")
    UserDto convertToUserDto(User user);


    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    UserProfile convertToUserProfile(UserPayload userPayload);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    void updateUserProfile(@MappingTarget UserProfile userProfile, UserProfilePayload userProfilePayload);
}
