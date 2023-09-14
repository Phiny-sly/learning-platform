package com.turntabl.labs.contentmanagement.exception;

public class MultimediaDoesNotExist extends RuntimeException {
    public MultimediaDoesNotExist(String filename) {
        super(String.format("Multimedia with url %s not found", filename));
    }
}
