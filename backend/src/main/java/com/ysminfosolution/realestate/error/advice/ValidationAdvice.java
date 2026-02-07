package com.ysminfosolution.realestate.error.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ysminfosolution.realestate.error.util.ValidationMessageBuilder;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationAdvice {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class
    })
    public ProblemDetail handleValidationExceptions(Exception ex) {

        FieldError error = ex instanceof MethodArgumentNotValidException manv
                ? manv.getBindingResult().getFieldErrors().get(0)
                : ((BindException) ex).getFieldErrors().get(0);

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Error");
        pd.setType(URI.create("https://api.realestate/errors/validation"));
        pd.setDetail(ValidationMessageBuilder.build(error));
        pd.setProperty("timestamp", Instant.now());

        return pd;
    }

}
