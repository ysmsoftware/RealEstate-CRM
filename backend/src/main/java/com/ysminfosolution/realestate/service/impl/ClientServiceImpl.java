package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ysminfosolution.realestate.dto.ClientBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ClientDetailsDTO;
import com.ysminfosolution.realestate.exception.NotFoundException;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.ClientUserInfoRepository;
import com.ysminfosolution.realestate.repository.EmployeeUserInfoRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FollowUpRepository;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientUserInfoRepository clientRepository;
    private final EmployeeUserInfoRepository employeeUserInfoRepository;

    private final EnquiryRepository enquiryRepository;
    private final FollowUpRepository followUpRepository;

    @Override
    public ClientUserInfo createClientForEnquiry(ClientUserInfo client) {

        log.info("\n");
        log.info("Method: createClientForEnquiry");

        client.setAadharNo(null);
        client.setPanNo(null);

        return clientRepository.save(client);
    }

    @Override
    public ResponseEntity<Set<ClientBasicInfoDTO>> getListOfClientBasicInfo(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getListOfClientBasicInfo");

        Set<ClientBasicInfoDTO> clientBasicInfoDTOs = new HashSet<>();

        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            clientBasicInfoDTOs = clientRepository.findClientBasicInfoByOrganization(appUserDetails.getOrgId());

        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            var employee = employeeUserInfoRepository
                    .findByUser_UserId(UUID.fromString(appUserDetails.getUserId()))
                    .orElse(null);

            if (employee != null && !employee.getProjects().isEmpty()) {
                var projectIds = employee.getProjects().stream()
                        .map(Project::getProjectId)
                        .collect(Collectors.toSet());

                clientBasicInfoDTOs = clientRepository.findClientBasicInfoByProjectIds(projectIds);
            }
        }

        return ResponseEntity.ok(clientBasicInfoDTOs);
    }

    @Override
    public ResponseEntity<ClientBasicInfoDTO> getClientBasicInfo(AppUserDetails appUserDetails, UUID clientId) {

        log.info("\n");
        log.info("Method: getClientBasicInfo");

        ClientUserInfo client = clientRepository.findByClientIdAndIsDeletedFalse(clientId).orElse(null);
        if (client == null) {
            log.error("Client with ID {} not found or is deleted.", clientId);
            throw new NotFoundException("Client not found");
        }

        ClientBasicInfoDTO basicInfoDTO = new ClientBasicInfoDTO(
                client.getClientId(),
                client.getClientName(),
                client.getMobileNumber(),
                client.getEmail(),
                client.getCity(),
                client.getOccupation());

        return ResponseEntity.ok(basicInfoDTO);
    }

    @Override
    public ResponseEntity<ClientDetailsDTO> getClientDetails(AppUserDetails appUserDetails, UUID clientId) {
        
        log.info("\n");
        log.info("Method: getClientDetails");

        ClientUserInfo client = clientRepository.findByClientIdAndIsDeletedFalse(clientId).orElse(null);
        if (client == null) {
            throw new NotFoundException("Client not found");
        }
        
        Set<Enquiry> enquiries = new HashSet<>();
        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            enquiries = enquiryRepository.findAllByClient_ClientIdAndIsDeletedFalse(clientId);
            
        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            var employee = employeeUserInfoRepository
                    .findByUser_UserId(UUID.fromString(appUserDetails.getUserId()))
                    .orElse(null);

            if (employee == null) {
                throw new NotFoundException("Employee not found");
            }

            enquiries = enquiryRepository.findAllByClientAndProjectsAndIsDeletedFalse(
                    client,
                    employee.getProjects());
        }
        Set<UUID> enquiryIds = enquiries.stream()
                .map(Enquiry::getEnquiryId)
                .collect(Collectors.toSet());

        Set<FollowUp> followUps = followUpRepository.findAllByEnquiriesWithFetch(enquiries);
        Set<UUID> followUpIds = followUps.stream()
                .map(FollowUp::getFollowUpId)
                .collect(Collectors.toSet());

        ClientDetailsDTO clientDetailsDTO = new ClientDetailsDTO(
            client.getClientId(),
            client.getClientName(),
            client.getEmail(),
            client.getMobileNumber(),
            client.getDob(),
            client.getLandlineNumber(),
            client.getCity(),
            client.getAddress(),
            client.getOccupation(),
            client.getCompany(),
            client.getPanNo(),
            client.getAadharNo(),
            enquiryIds,
            followUpIds
        );

        return ResponseEntity.ok(clientDetailsDTO);
    }

}
