package com.phiny.labs.usermanagement.controller;

import com.phiny.labs.common.security.SecurityUtils;
import com.phiny.labs.usermanagement.dto.TokenResponse;
import com.phiny.labs.usermanagement.dto.UserDto;
import com.phiny.labs.usermanagement.payload.LoginPayload;
import com.phiny.labs.usermanagement.payload.RefreshTokenPayload;
import com.phiny.labs.usermanagement.payload.PasswordResetPayload;
import com.phiny.labs.usermanagement.payload.PasswordResetRequestPayload;
import com.phiny.labs.usermanagement.payload.UserPayload;
import com.phiny.labs.usermanagement.payload.UserProfilePayload;
import com.phiny.labs.usermanagement.payload.UserRolePayload;
import com.phiny.labs.usermanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserPayload userPayload) {
        return new ResponseEntity<>(userService.createUser(userPayload), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginPayload loginPayload) {
        return new ResponseEntity<>(userService.login(loginPayload), HttpStatus.ACCEPTED);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenPayload payload) {
        return new ResponseEntity<>(userService.refreshToken(payload.getRefreshToken()), HttpStatus.OK);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        Sort sortObj = Sort.by(sort[0].split(",")[0]);
        if (sort[0].split(",").length > 1 && sort[0].split(",")[1].equalsIgnoreCase("desc")) {
            sortObj = sortObj.descending();
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PutMapping("/details-change")
    public ResponseEntity<UserDto> updateUserDetails(@RequestBody UserProfilePayload userProfilePayload) {
        // Get current user from security context - no ID needed
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // Security check is done in service layer - users can only update their own details
        return new ResponseEntity<>(userService.updateUserDetails(currentUserId, userProfilePayload), HttpStatus.ACCEPTED);
    }

    @PutMapping("/role-change")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRolePayload userRolePayload) {
        return new ResponseEntity<>(userService.updateUserRole(userRolePayload), HttpStatus.ACCEPTED);
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequestPayload payload) {
        userService.requestPasswordReset(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetPayload payload) {
        userService.resetPassword(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
