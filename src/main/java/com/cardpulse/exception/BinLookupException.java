package com.cardpulse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BinLookupException extends RuntimeException {
    public BinLookupException(String message) {
        super(message);
    }
}
