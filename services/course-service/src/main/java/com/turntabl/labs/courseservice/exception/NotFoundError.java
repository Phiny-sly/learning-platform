package com.turntabl.labs.courseservice.exception;

import java.util.UUID;

public class NotFoundError extends RuntimeException{

    public NotFoundError() {
        super("object not found");
    }

    public NotFoundError(String message) {
        super(message);
    }

    public NotFoundError(UUID id) {
        super("object with Id "+id.toString()+" not found");
    }

}
