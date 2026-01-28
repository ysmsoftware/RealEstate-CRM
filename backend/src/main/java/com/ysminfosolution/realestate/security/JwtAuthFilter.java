package com.ysminfosolution.realestate.security;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository users;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // No token → just continue without authentication
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // --- AUTHENTICATION PHASE (Errors here → 401) ---
            JWTClaimsSet claims = jwtService.validate(token);

            UUID orgId = UUID.fromString(claims.getStringClaim("orgId"));
            UUID userId = UUID.fromString(claims.getStringClaim("userId"));

            User user = users
                    .findByOrganization_OrgIdAndUserIdAndIsDeletedFalseAndEnabledTrue(orgId, userId)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            AppUserDetails details = new AppUserDetails(user);

            if (!details.isAccountNonLocked()) {
                throw new BadCredentialsException("User account is locked");
            }

            if (!details.isAccountNonExpired()) {
                throw new BadCredentialsException("User account is expired");
            }

            // --- AUTH SUCCESS ---
            var auth = new UsernamePasswordAuthenticationToken(
                    details, null, details.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (BadCredentialsException | ParseException e) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException(e.getMessage(), e);

        }

        chain.doFilter(request, response);
    }
}
