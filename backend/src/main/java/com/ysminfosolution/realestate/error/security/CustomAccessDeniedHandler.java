package com.ysminfosolution.realestate.error.security;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
            throws IOException {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Forbidden");
        pd.setDetail("You do not have permission to access this resource");
        try {
            pd.setType(new URI("https://api.realestate/errors/forbidden"));
            pd.setInstance(new URI(req.getRequestURI()));
        } catch (Exception e) {
            // Invalid URI format
        }
        pd.setProperty("timestamp", Instant.now());

        res.setStatus(403);
        res.setContentType("application/problem+json");
        mapper.writeValue(res.getOutputStream(), pd);
    }
}
