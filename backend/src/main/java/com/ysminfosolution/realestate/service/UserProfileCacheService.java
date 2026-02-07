package com.ysminfosolution.realestate.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ysminfosolution.realestate.dto.NewOrganizationResponseDTO;
import com.ysminfosolution.realestate.dto.UserResponseDTO;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Organization;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.OrganizationRepository;
import com.ysminfosolution.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileCacheService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @SuppressWarnings("null")
    public Organization getOrganizationById(UUID orgId) {
        return organizationRepository.findById(orgId)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new NotFoundException("Organization not found"));
    }

    @SuppressWarnings("null")
    public UserResponseDTO getUserProfile(UUID userId, UUID orgId) {

        Organization organization = getOrganizationById(orgId);

        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return new UserResponseDTO(
                user.getUserId(),
                new NewOrganizationResponseDTO(
                        organization.getOrgId(),
                        organization.getOrgName(),
                        organization.getOrgEmail()),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getMobileNumber(),
                user.getRole(),
                user.isEnabled(),
                null);
    }
}
