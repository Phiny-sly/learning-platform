package com.turntabl.labs.usermanagement.controller;

import com.turntabl.labs.usermanagement.dto.UserDto;
import com.turntabl.labs.usermanagement.payload.UserPayload;
import com.turntabl.labs.usermanagement.payload.UserProfilePayload;
import com.turntabl.labs.usermanagement.payload.UserRolePayload;
import com.turntabl.labs.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserPayload userPayload) {
        return new ResponseEntity<>(userService.createUser(userPayload), HttpStatus.CREATED);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/details-change/{id}")
    public ResponseEntity<UserDto> updateUserDetails(@PathVariable("id") long userId, @RequestBody UserProfilePayload userProfilePayload) {
        return new ResponseEntity<>(userService.updateUserDetails(userId, userProfilePayload), HttpStatus.ACCEPTED);
    }

    @PutMapping("/role-change")
    public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRolePayload userRolePayload) {
        return new ResponseEntity<>(userService.updateUserRole(userRolePayload), HttpStatus.ACCEPTED);
    }

}
