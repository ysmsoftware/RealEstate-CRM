package com.ysminfosolution.realestate.error.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ysminfosolution.realestate.error.exception.ApiException;

import java.net.URI;
import java.time.Instant;

@SuppressWarnings("null")

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApplicationAdvice {

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                ex.getStatus(), ex.getMessage());

        pd.setTitle(ex.getStatus().getReasonPhrase());
        pd.setType(URI.create("https://api.realestate/errors/" + ex.getStatus().value()));
        pd.setProperty("timestamp", Instant.now());

        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {

        if (ex instanceof AuthorizationDeniedException) {
            throw (AuthorizationDeniedException) ex;
        }

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");

        pd.setTitle("Internal Server Error");
        pd.setType(URI.create("https://api.realestate/errors/server-error"));
        pd.setProperty("timestamp", Instant.now());

        return pd;
    }

}
