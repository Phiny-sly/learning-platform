package com.phiny.labs.usermanagement.service;

import com.phiny.labs.common.security.SecurityUtils;
import com.phiny.labs.usermanagement.config.EntityMapper;
import com.phiny.labs.usermanagement.dto.TokenResponse;
import com.phiny.labs.usermanagement.dto.UserDto;
import com.phiny.labs.usermanagement.entity.PasswordResetCode;
import com.phiny.labs.usermanagement.entity.RefreshToken;
import com.phiny.labs.usermanagement.entity.User;
import com.phiny.labs.usermanagement.entity.UserProfile;
import com.phiny.labs.usermanagement.exception.AccessDeniedException;
import com.phiny.labs.usermanagement.exception.EmailAlreadyExistsException;
import com.phiny.labs.usermanagement.exception.RoleAlreadyExistsException;
import com.phiny.labs.usermanagement.exception.UserNotFoundException;
import com.phiny.labs.usermanagement.payload.LoginPayload;
import com.phiny.labs.usermanagement.payload.PasswordResetPayload;
import com.phiny.labs.usermanagement.payload.PasswordResetRequestPayload;
import com.phiny.labs.usermanagement.payload.UserPayload;
import com.phiny.labs.usermanagement.payload.UserProfilePayload;
import com.phiny.labs.usermanagement.payload.UserRolePayload;
import com.phiny.labs.usermanagement.repository.PasswordResetCodeRepository;
import com.phiny.labs.usermanagement.repository.UserProfileRepository;
import com.phiny.labs.usermanagement.repository.UserRepository;
import com.phiny.labs.usermanagement.repository.UserRoleRepository;
import com.phiny.labs.usermanagement.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtGeneratorService jwtGeneratorService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public UserDto createUser(UserPayload userPayload) {
        userProfileRepository.findByEmail(userPayload.getEmail()).ifPresent(userProfile -> {
            throw new EmailAlreadyExistsException(userPayload.getEmail());
        });
        User user = new User();
        user.setUserRole(userRoleRepository.findByRoleName(userPayload.getRole())
                .orElseThrow());
        user.setTenantId(userPayload.getTenantId());
        UserProfile userProfile = EntityMapper.INSTANCE.convertToUserProfile(userPayload);
        userProfile.setPassword(bCryptPasswordEncoder.encode(userPayload.getPassword()));
        user.setUserProfile(userProfile);
        userRepository.save(user);
        return EntityMapper.INSTANCE.convertToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserDetails(long userId, UserProfilePayload userProfilePayload) {
        // Security check: Ensure user can only update their own details (unless admin)
        if (!SecurityUtils.canAccess(userId)) {
            throw new AccessDeniedException("You can only update your own user details");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userProfileRepository.findById(user.getUserProfile().getId()).ifPresent(userprofile -> {
            EntityMapper.INSTANCE.updateUserProfile(userprofile, userProfilePayload);
            if (userProfilePayload.getPassword() != null) {
                userprofile.setPassword(bCryptPasswordEncoder.encode(userProfilePayload.getPassword()));
            }
            userProfileRepository.save(userprofile);
        });
        return EntityMapper.INSTANCE.convertToUserDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        // Security check: Ensure user can only view their own details (unless admin)
        if (!SecurityUtils.canAccess(userId)) {
            throw new AccessDeniedException("You can only view your own user details");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return EntityMapper.INSTANCE.convertToUserDto(user);
    }

    //admin user can update user role
    @Override
    @Transactional
    public UserDto updateUserRole(UserRolePayload userRolePayload) {
        var ref = new Object() {
            User userResult = null;
        };
        userProfileRepository.findByEmail(userRolePayload.getEmail())
                .ifPresentOrElse(userProfile -> userRepository.findAll()
                        .stream()
                        .filter(user -> Objects.equals(user.getUserProfile()
                                .getId(), userProfile.getId()))
                        .findAny().ifPresent(user -> {
                            if (user.getUserRole()
                                    .getRoleName()
                                    .name()
                                    .equalsIgnoreCase(userRolePayload.getRole())) {
                                throw new RoleAlreadyExistsException(userRolePayload.getRole());
                            }
                            user.setUserRole(userRoleRepository.findByRoleName(userRolePayload.getRole())
                                    .orElseThrow());
                            ref.userResult = user;
                            userRepository.save(user);
                        }), () -> {
                    throw new UserNotFoundException(userRolePayload.getEmail());
                });
        return EntityMapper.INSTANCE.convertToUserDto(ref.userResult);
    }

    @Override
    public String generateToken(LoginPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getEmail(), payload.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDto user = getUserByEmail(payload.getEmail());
            String role = user.getRole() != null ? user.getRole() : "STUDENT";
            Long userId = user.getId();
            Long tenantId = user.getTenantId();
            return jwtGeneratorService.generateToken(payload.getEmail(), role, userId, tenantId);
        }
        throw new UserNotFoundException();
    }

    @Override
    public TokenResponse login(LoginPayload payload) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getEmail(), payload.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDto user = getUserByEmail(payload.getEmail());
            String role = user.getRole() != null ? user.getRole() : "STUDENT";
            Long userId = user.getId();
            Long tenantId = user.getTenantId();
            
            String accessToken = jwtGeneratorService.generateToken(payload.getEmail(), role, userId, tenantId);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);
            
            return new TokenResponse(accessToken, refreshToken.getToken(), "Bearer", 86400L);
        }
        throw new UserNotFoundException();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        
        User user = refreshToken.getUser();
        UserDto userDto = EntityMapper.INSTANCE.convertToUserDto(user);
        
        String role = userDto.getRole() != null ? userDto.getRole() : "STUDENT";
        String accessToken = jwtGeneratorService.generateToken(userDto.getEmail(), role, userDto.getId(), userDto.getTenantId());
        
        // Optionally rotate refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        return new TokenResponse(accessToken, newRefreshToken.getToken(), "Bearer", 86400L);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findAll().stream().filter(a -> a.getUserProfile().getEmail().matches(email)).findFirst();
        User result = null;
        if (user.isPresent()) {
            result = user.get();
            
            // Security check: Ensure user can only view their own details (unless admin)
            if (!SecurityUtils.canAccess(result.getId())) {
                throw new AccessDeniedException("You can only view your own user details");
            }
        }
        return EntityMapper.INSTANCE.convertToUserDto(result);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProfileRepository.findByEmail(username).orElseThrow();
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        List<UserDto> userDtos = users.getContent().stream()
                .map(EntityMapper.INSTANCE::convertToUserDto)
                .collect(Collectors.toList());
        return new PageImpl<>(userDtos, pageable, users.getTotalElements());
    }

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequestPayload payload) {
        UserProfile userProfile = userProfileRepository.findByEmail(payload.getEmail())
                .orElseThrow(() -> new UserNotFoundException(payload.getEmail()));
        
        User user = userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getUserProfile().getId(), userProfile.getId()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(payload.getEmail()));

        // Delete existing reset code if any
        passwordResetCodeRepository.findByUser(user).ifPresent(passwordResetCodeRepository::delete);

        // Generate new reset code
        String resetCode = UUID.randomUUID().toString();
        PasswordResetCode passwordResetCode = new PasswordResetCode();
        passwordResetCode.setId(resetCode);
        passwordResetCode.setUser(user);
        passwordResetCode.setExpirationDate(LocalDateTime.now().plusHours(1)); // 1 hour expiry
        passwordResetCodeRepository.save(passwordResetCode);
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetPayload payload) {
        UserProfile userProfile = userProfileRepository.findByEmail(payload.getEmail())
                .orElseThrow(() -> new UserNotFoundException(payload.getEmail()));

        User user = userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getUserProfile().getId(), userProfile.getId()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(payload.getEmail()));

        PasswordResetCode resetCode = passwordResetCodeRepository.findByIdAndUser(payload.getCode(), user)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset code"));

        if (resetCode.getExpirationDate().isBefore(LocalDateTime.now())) {
            passwordResetCodeRepository.delete(resetCode);
            throw new RuntimeException("Reset code has expired");
        }

        // Update password
        userProfile.setPassword(bCryptPasswordEncoder.encode(payload.getNewPassword()));
        userProfileRepository.save(userProfile);

        // Delete reset code after use
        passwordResetCodeRepository.delete(resetCode);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.delete(user);
    }
}
