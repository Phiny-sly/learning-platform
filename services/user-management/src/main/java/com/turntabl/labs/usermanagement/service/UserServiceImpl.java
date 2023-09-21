package com.turntabl.labs.usermanagement.service;

import com.turntabl.labs.usermanagement.config.EntityMapper;
import com.turntabl.labs.usermanagement.dto.UserDto;
import com.turntabl.labs.usermanagement.entity.User;
import com.turntabl.labs.usermanagement.entity.UserProfile;
import com.turntabl.labs.usermanagement.exception.EmailAlreadyExistsException;
import com.turntabl.labs.usermanagement.exception.RoleAlreadyExistsException;
import com.turntabl.labs.usermanagement.exception.UserNotFoundException;
import com.turntabl.labs.usermanagement.payload.LoginPayload;
import com.turntabl.labs.usermanagement.payload.UserPayload;
import com.turntabl.labs.usermanagement.payload.UserProfilePayload;
import com.turntabl.labs.usermanagement.payload.UserRolePayload;
import com.turntabl.labs.usermanagement.repository.UserProfileRepository;
import com.turntabl.labs.usermanagement.repository.UserRepository;
import com.turntabl.labs.usermanagement.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

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

    @Override
    public UserDto createUser(UserPayload userPayload) {
        userProfileRepository.findByEmail(userPayload.getEmail()).ifPresent(userProfile -> {
            throw new EmailAlreadyExistsException(userPayload.getEmail());
        });
        User user = new User();
        user.setUserRole(userRoleRepository.findByRoleName(userPayload.getRole())
                .orElseThrow());
        UserProfile userProfile = EntityMapper.INSTANCE.convertToUserProfile(userPayload);
        userProfile.setPassword(bCryptPasswordEncoder.encode(userProfile.getPassword()));
        user.setUserProfile(userProfile);
        userRepository.save(user);
        return EntityMapper.INSTANCE.convertToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserDetails(long userId, UserProfilePayload userProfilePayload) {
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
            return jwtGeneratorService.generateToken(payload.getEmail());
        }
        throw new UserNotFoundException();
        //        UserProfile userProfile = userProfileRepository.findByEmail(payload.getEmail()).orElseThrow(UserNotFoundException::new);
//
        //        boolean matches = bCryptPasswordEncoder.matches(payload.getPassword(), userProfile.getPassword());
//        if (!matches) throw new UserNotFoundException();
//        Optional<User> user = userRepository.findAll().stream().filter(a -> a.getUserProfile().getEmail().matches(userProfile.getEmail())).findFirst();
//        User result = null;
//        if (user.isPresent()) {
//            result = user.get();
//        }
//        return EntityMapper.INSTANCE.convertToUserDto(result);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findAll().stream().filter(a -> a.getUserProfile().getEmail().matches(email)).findFirst();
        User result = null;
        if (user.isPresent()) {
            result = user.get();
        }
        return EntityMapper.INSTANCE.convertToUserDto(result);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProfileRepository.findByEmail(username).orElseThrow();
    }
}
