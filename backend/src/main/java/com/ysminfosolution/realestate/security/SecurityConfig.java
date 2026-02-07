package com.ysminfosolution.realestate.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ysminfosolution.realestate.error.security.CustomAccessDeniedHandler;
import com.ysminfosolution.realestate.error.security.CustomAuthenticationEntryPoint;
import com.ysminfosolution.realestate.repository.UserRepository;
import com.ysminfosolution.realestate.security.filter.JsonUsernamePasswordAuthFilter;
import com.ysminfosolution.realestate.security.handler.JwtAuthenticationFailureHandler;
import com.ysminfosolution.realestate.security.handler.JwtAuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationSuccessHandler successHandler;
    private final JwtAuthenticationFailureHandler failureHandler;

    @Bean
    SecurityFilterChain securityFilterChain(
            AuthenticationManager authenticationManager,
            HttpSecurity http)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/register-organization", "/refresh", "/status", "/index.html",
                            "/dashboard.js")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler))
            .addFilterBefore(jsonLoginFilter(authenticationManager),
                    UsernamePasswordAuthenticationFilter.class)

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("deprecation")
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return username -> {

            return users.findByUsernameAndIsDeletedFalseAndEnabledTrueAndOrganization_IsDeletedFalse(username)
                    .map(AppUserDetails::new)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }

    @Bean
    JsonUsernamePasswordAuthFilter jsonLoginFilter(AuthenticationManager am) {
        JsonUsernamePasswordAuthFilter filter = new JsonUsernamePasswordAuthFilter(am);

        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }

}
