package com.cardpulse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidLimitException extends RuntimeException {
    public InvalidLimitException(String message) {
        super(message);
    }
}
