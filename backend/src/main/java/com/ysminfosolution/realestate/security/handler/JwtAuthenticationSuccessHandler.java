package com.ysminfosolution.realestate.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysminfosolution.realestate.controller.AuthController.TokenResponse;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final JwtService jwt;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        AppUserDetails user =
            (AppUserDetails) authentication.getPrincipal();

        TokenResponse body = new TokenResponse(
            jwt.generateAccessToken(user),
            jwt.generateRefreshToken(user),
            900,
            user.getRole()
        );

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
