package com.ysminfosolution.realestate.error.security;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Unauthorized");
        pd.setDetail(cleanMessage(authException));
        try {
            pd.setType(new URI("https://api.realestate/errors/authentication"));
            pd.setInstance(new URI(request.getRequestURI()));
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI syntax", e);
        }
        pd.setProperty("timestamp", Instant.now());

        response.setStatus(401);
        response.setContentType("application/problem+json");
        objectMapper.writeValue(response.getOutputStream(), pd);
    }

    private String cleanMessage(AuthenticationException e) {
        return switch (e.getClass().getSimpleName()) {
            case "BadCredentialsException" -> "Invalid username or password OR Token expired";
            case "CredentialsExpiredException" -> "Credentials expired";
            case "DisabledException" -> "Account disabled";
            default -> "Authentication failed";
        };
    }
}
