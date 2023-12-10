package com.cardpulse.exception.advice;

import com.cardpulse.payload.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiErrorResponse handleInvalidRequest(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ){
        ApiErrorResponse apiError = new ApiErrorResponse(
                request.getRequestURI(),
                Collections.singletonList(e.getMessage()),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                LocalDateTime.now()
        );
        log.info("Invalid Request Error - {}", apiError);
        return apiError;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleInvalidArgument(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ){
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiErrorResponse apiError = new ApiErrorResponse(
                request.getRequestURI(),
                errors,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        log.info("Invalid Argument Error - {}", apiError);
        return apiError;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ApiErrorResponse handleInsufficientAuthenticationException(
            InsufficientAuthenticationException e,
            HttpServletRequest request) {

        return new ApiErrorResponse(
                request.getRequestURI(),
                Collections.singletonList(e.getMessage()),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ApiErrorResponse handleBadCredentialsException(
            BadCredentialsException e,
            HttpServletRequest request) {

        return new ApiErrorResponse(
                request.getRequestURI(),
                Collections.singletonList(e.getMessage()),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleException(
            Exception e,
            HttpServletRequest request) {

        return new ApiErrorResponse(
                request.getRequestURI(),
                Collections.singletonList(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
    }

}
