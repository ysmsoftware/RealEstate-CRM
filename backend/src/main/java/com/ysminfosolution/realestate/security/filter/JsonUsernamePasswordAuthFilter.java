package com.ysminfosolution.realestate.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// * Authentication happens here
public class JsonUsernamePasswordAuthFilter
        extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUsernamePasswordAuthFilter(AuthenticationManager authManager) {
        setAuthenticationManager(authManager);
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            LoginRequest creds =
                objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    creds.username(),
                    creds.password()
                );

            return getAuthenticationManager().authenticate(auth);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login request", e);
        }
    }

    record LoginRequest(String username, String password) {}
}
