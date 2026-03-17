package com.mensajeria.controller.exception;

import com.mensajeria.model.exception.UserNotFoundException;
import org.springframework.security.core.AuthenticationException;
import com.mensajeria.security.exception.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public DTOResponseError userNotFound(UserNotFoundException ex) {return new DTOResponseError(ex.getMessage());}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenException.class)
    public DTOResponseError invalidToken(InvalidTokenException ex) {return new DTOResponseError(ex.getMessage());}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthenticationException.class)
    public DTOResponseError authenticationException(AuthenticationException ex) {return new DTOResponseError(ex.getMessage());}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public DTOResponseError illegalArgumentException(IllegalArgumentException ex) {return new DTOResponseError(ex.getMessage());}
}
