package com.phiny.labs.courseservice.exception;

import com.phiny.labs.common.exception.*;
import jakarta.ws.rs.BadRequestException;
import org.modelmapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@ResponseBody
public class Advisor {

    private static final Logger logger = LoggerFactory.getLogger(Advisor.class);

    @ExceptionHandler(value = {NotFoundError.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage notFoundHandler(NoHandlerFoundException  er){
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), er.getMessage());
    }

    @ExceptionHandler(value = {MappingException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage mappingErrorHandler(RuntimeException e, WebRequest wr){
        logger.warn("Mapping error: {}", e.getMessage());
        return new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), "unacceptable request body");
    }

    @ExceptionHandler(value = {UnauthorisedError.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorisedErrorHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(value = {InternalServerError.class, ServiceException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage serviceErrorHandler(RuntimeException e, WebRequest wr){
        logger.error("Service error: {}", e.getMessage(), e);
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }
    
    @ExceptionHandler(value = {ExternalServiceException.class})
    @ResponseStatus(value = HttpStatus.BAD_GATEWAY)
    public ErrorMessage externalServiceErrorHandler(ExternalServiceException e, WebRequest wr){
        logger.error("External service error [{}]: {}", e.getServiceName(), e.getMessage(), e);
        return new ErrorMessage(HttpStatus.BAD_GATEWAY.value(), e.getMessage());
    }
    
    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage validationErrorHandler(ValidationException e, WebRequest wr){
        logger.warn("Validation error: {}", e.getMessage());
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
    
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundHandler(ResourceNotFoundException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
    
    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage nullPointerHandler(NullPointerException e, WebRequest wr){
        logger.error("Null pointer exception: {}", e.getMessage(), e);
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An internal error occurred");
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorMessage daoConflictHandler(RuntimeException e, WebRequest wr){
        return new ErrorMessage(HttpStatus.CONFLICT.value(), e.getMessage());
    }


    @ExceptionHandler(value = {BadRequestException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage badRequestHandler(Exception e){
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}
