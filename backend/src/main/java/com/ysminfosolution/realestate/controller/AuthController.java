package com.ysminfosolution.realestate.controller;

import java.text.ParseException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ysminfosolution.realestate.model.User.Role;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.security.JwtService;
import com.ysminfosolution.realestate.security.OrganizationUserDetailsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final OrganizationUserDetailsService organizationUserDetailsService;
    private final JwtService jwt;

    // ---------- DTOs ----------

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {
    }

    public record TokenResponse(String accessToken, String refreshToken, long expiresInSeconds, Role role) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record OrgOption(UUID orgId, String orgName) {
    }

    public record ConflictResponse(Set<OrgOption> organizations) {
    }

    // ---------- Endpoints ----------

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) throws ParseException {
        JWTClaimsSet claims = jwt.validate(req.refreshToken());

        log.info("\n");
        log.info("Path: [POST] /refresh | Method: refresh");

        // Ensure it's a refresh token (we set jti = "refresh" in JwtService)
        if (!"refresh".equals(Optional.ofNullable(claims.getJWTID()).orElse(""))) {
            return ResponseEntity.badRequest().build();
        }

        UUID orgId = UUID.fromString(claims.getStringClaim("orgId"));
        UUID userId = UUID.fromString(claims.getStringClaim("userId"));

        // Reload user (ensures user still active and not deleted)
        UserDetails details = organizationUserDetailsService.load(orgId, userId);
        AppUserDetails user = (AppUserDetails) details;

        String access = jwt.generateAccessToken(user);
        String refresh = jwt.generateRefreshToken(user);

        return ResponseEntity.ok(new TokenResponse(access, refresh, 900, user.getRole()));
    }
}