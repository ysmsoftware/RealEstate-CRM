package com.ysminfosolution.realestate.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationUserDetailsService {
  private final UserRepository users;

  public UserDetails load(UUID orgId, UUID userId) {
    User user = users.findByOrganization_OrgIdAndUserIdAndIsDeletedFalseAndEnabledTrue(orgId, userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new AppUserDetails(user);
  }

}
