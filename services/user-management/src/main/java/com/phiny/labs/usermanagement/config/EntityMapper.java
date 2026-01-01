package com.phiny.labs.usermanagement.config;

import com.phiny.labs.usermanagement.payload.UserPayload;
import com.phiny.labs.usermanagement.dto.UserDto;
import com.phiny.labs.usermanagement.entity.User;
import com.phiny.labs.usermanagement.entity.UserProfile;
import com.phiny.labs.usermanagement.payload.UserProfilePayload;
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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getFirstName() : null)")
    @Mapping(target = "lastName", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getLastName() : null)")
    @Mapping(target = "avatarUrl", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getAvatarUrl() : null)")
    @Mapping(target = "phoneNumber", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getPhoneNumber() : null)")
    @Mapping(target = "email", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getEmail() : null)")
    @Mapping(target = "role", expression = "java(user.getUserRole() != null ? user.getUserRole().getRoleName().name() : null)")
    @Mapping(target = "tenantId", source = "tenantId")
    @Mapping(target = "createdAt", expression = "java(user.getCreatedAt())")
    @Mapping(target = "updatedAt", expression = "java(user.getUpdatedAt())")
    @Mapping(target = "grantedAuthorities", expression = "java(user.getUserProfile() != null ? user.getUserProfile().getAuthorities() : null)")
    UserDto convertToUserDto(User user);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserProfile convertToUserProfile(UserPayload userPayload);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserProfile(@MappingTarget UserProfile userProfile, UserProfilePayload userProfilePayload);
}
