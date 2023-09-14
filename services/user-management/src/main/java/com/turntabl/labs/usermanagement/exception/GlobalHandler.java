package com.turntabl.labs.usermanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class GlobalHandler {

    @ExceptionHandler(value = {UserNotFoundException.class, UserProfileDoesNotExist.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerError notFoundHandler(RuntimeException ex) {
        return new ControllerError(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(value = {RoleAlreadyExistsException.class, EmailAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ControllerError alreadyExistsHandler(RuntimeException ex) {
        return new ControllerError(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
