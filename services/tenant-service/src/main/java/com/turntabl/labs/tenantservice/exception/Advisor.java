package com.turntabl.labs.tenantservice.exception;

import org.modelmapper.MappingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@ResponseBody
public class Advisor {

    @ExceptionHandler(value = {NotFoundError.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = {MappingException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage mappingErrorHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), "unacceptable request body");
    }

    @ExceptionHandler(value = {InternalServerError.class, NullPointerException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage jsonProcessingHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage daoConflictHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.CONFLICT.value(), "data object already exists -> "+e.getMessage());
    }

}
