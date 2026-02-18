package com.ysminfosolution.realestate.service.impl;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ysminfosolution.realestate.dto.NewOrganizationRequestDTO;
import com.ysminfosolution.realestate.dto.NewOrganizationResponseDTO;
import com.ysminfosolution.realestate.error.exception.ConflictException;
import com.ysminfosolution.realestate.model.AdminUserInfo;
import com.ysminfosolution.realestate.model.Organization;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.AdminUserInfoRepository;
import com.ysminfosolution.realestate.repository.OrganizationRepository;
import com.ysminfosolution.realestate.repository.UserRepository;
import com.ysminfosolution.realestate.service.OrganizationService;
import com.ysminfosolution.realestate.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
    
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final AdminUserInfoRepository adminUserInfoRepository;
    private final S3StorageService s3StorageService;

    @Override
    public ResponseEntity<NewOrganizationResponseDTO> createNewOrganizationAndAdmin(
            NewOrganizationRequestDTO newOrganizationRequestDTO,
            MultipartFile logo) {

        log.info("\n");
        log.info("Method: createNewOrganizationAndAdmin");



        // ^ Duplicate orgEmail is NOT allowed
        if (organizationRepository.existsByOrgEmail(newOrganizationRequestDTO.orgEmail())) {
            throw new ConflictException("Duplicate orgEmail is NOT allowed");
        }

        // ^ Duplicate username is NOT allowed
        if (userRepository.existsByUsername(newOrganizationRequestDTO.username())) {
            throw new ConflictException("Duplicate username is NOT allowed");
        }

        Organization organization = new Organization();
        organization.setOrgName(newOrganizationRequestDTO.orgName());
        organization.setOrgEmail(newOrganizationRequestDTO.orgEmail());
        organization.setDeleted(false);
        
        organization = organizationRepository.save(organization);
        
        if (logo != null && !logo.isEmpty()) {
            organization.setLogoUrl(uploadOrganizationLogo(organization.getOrgId(), logo));
            organization = organizationRepository.save(organization);
        }
        

        // Cache the newly created organization
        // Note: CachePut will update cache if key exists, or create new entry

        User user = new User();
        user.setEmail(newOrganizationRequestDTO.email());
        user.setUsername(newOrganizationRequestDTO.username());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(newOrganizationRequestDTO.password()));
        user.setFullName(newOrganizationRequestDTO.fullName());
        user.setMobileNumber(newOrganizationRequestDTO.mobileNo());
        user.setRole(User.Role.ADMIN);
        user.setOrganization(organization);
        user.setEnabled(true);
        user.setDeleted(false);

        user = userRepository.save(user);

        AdminUserInfo adminUserInfo = new AdminUserInfo();
        adminUserInfo.setUser(user);
        adminUserInfo.setDeleted(false);
        adminUserInfo.setSuperAdmin(true);

        adminUserInfoRepository.save(adminUserInfo);

        NewOrganizationResponseDTO responseDTO = new NewOrganizationResponseDTO(
            organization.getOrgId(), 
            organization.getOrgName(), 
            organization.getOrgEmail()
        );
        return ResponseEntity.ok(responseDTO);
    }

    private String uploadOrganizationLogo(UUID orgId, MultipartFile logo) {
        String originalFilename = logo.getOriginalFilename() == null ? "logo" : logo.getOriginalFilename().trim();
        String normalizedFilename = originalFilename
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "");
        if (normalizedFilename.isBlank()) {
            normalizedFilename = "logo";
        }
        String key = orgId + "/organization/logo/" + UUID.randomUUID() + "-" + normalizedFilename;
        return s3StorageService.uploadFile(key, logo);
    }

}
