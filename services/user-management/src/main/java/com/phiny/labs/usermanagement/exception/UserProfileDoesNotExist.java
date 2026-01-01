package com.phiny.labs.usermanagement.exception;

public class UserProfileDoesNotExist extends RuntimeException {
    public UserProfileDoesNotExist(long id) {
        super(String.format("User profile with id %d does not exist", id));
    }
}
