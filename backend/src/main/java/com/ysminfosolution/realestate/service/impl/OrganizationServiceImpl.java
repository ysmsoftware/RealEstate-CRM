package com.ysminfosolution.realestate.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public ResponseEntity<NewOrganizationResponseDTO> createNewOrganizationAndAdmin(NewOrganizationRequestDTO newOrganizationRequestDTO) {

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

}
