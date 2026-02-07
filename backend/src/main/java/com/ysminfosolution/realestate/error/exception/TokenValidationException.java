package com.ysminfosolution.realestate.error.exception;

import org.springframework.http.HttpStatus;

public class TokenValidationException extends ApiException {
    public TokenValidationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}