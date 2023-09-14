package com.turntabl.labs.courseservice.exception;

public class UnauthorisedError extends RuntimeException {
    public UnauthorisedError() {
        super("Permission denied");
    }
}
