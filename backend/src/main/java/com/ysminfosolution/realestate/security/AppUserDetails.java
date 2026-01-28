package com.ysminfosolution.realestate.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ysminfosolution.realestate.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // map enum role -> "ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_CLIENT"
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getFullName() {
        return user.getFullName();
    }

    public String getMobileNumber() {
        return user.getMobileNumber();
    }

    public UUID getOrgId() {
        return user.getOrganization().getOrgId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public User.Role getRole() {
        return user.getRole();
    }

    public String getUserId() {
        return user.getUserId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled() && !user.isDeleted();
    }
}
