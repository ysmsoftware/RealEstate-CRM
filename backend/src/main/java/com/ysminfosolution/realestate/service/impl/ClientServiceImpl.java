package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ysminfosolution.realestate.dto.ClientBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ClientDetailsDTO;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Booking;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.BookingRepository;
import com.ysminfosolution.realestate.repository.ClientUserInfoRepository;
import com.ysminfosolution.realestate.repository.EmployeeUserInfoRepository;
import com.ysminfosolution.realestate.repository.FollowUpRepository;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ClientService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientUserInfoRepository clientRepository;
    private final EmployeeUserInfoRepository employeeUserInfoRepository;
    private final BookingRepository bookingRepository;
    private final FollowUpRepository followUpRepository;

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

        ClientUserInfo client = clientRepository.findByClientIdAndIsDeletedFalse(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        ensureAuthorizedClientAccess(clientId, appUserDetails);

        return ResponseEntity.ok(new ClientBasicInfoDTO(
                client.getClientId(),
                client.getClientName(),
                client.getMobileNumber(),
                client.getEmail(),
                client.getCity(),
                client.getOccupation()));
    }

    @Override
    public ResponseEntity<ClientDetailsDTO> getClientDetails(AppUserDetails appUserDetails, UUID clientId) {

        log.info("\n");
        log.info("Method: getClientDetails");

        Set<Booking> bookings = getAuthorizedBookings(clientId, appUserDetails);
        ClientUserInfo client = bookings.iterator().next().getClient();

        Set<Enquiry> enquiries = bookings.stream()
                .map(Booking::getEnquiry)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<UUID> enquiryIds = enquiries.stream()
                .map(Enquiry::getEnquiryId)
                .collect(Collectors.toSet());

        Set<UUID> followUpIds = enquiries.isEmpty()
                ? Set.of()
                : followUpRepository.findAllByEnquiriesWithFetch(enquiries).stream()
                        .map(FollowUp::getFollowUpId)
                        .collect(Collectors.toSet());

        return ResponseEntity.ok(new ClientDetailsDTO(
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
                followUpIds));
    }

    @Transactional
    @Override
    public ResponseEntity<String> changeClientInfo(
            UUID clientId,
            ClientDetailsDTO clientInfo,
            AppUserDetails appUserDetails) {

        log.info("Method: changeClientInfo");

        Set<Booking> bookings = getAuthorizedBookings(clientId, appUserDetails);
        ClientUserInfo client = bookings.iterator().next().getClient();

        updateClientFields(client, clientInfo);
        clientRepository.save(client);

        return ResponseEntity.ok("Client information updated successfully");
    }

    private void ensureAuthorizedClientAccess(UUID clientId, AppUserDetails appUserDetails) {
        getAuthorizedBookings(clientId, appUserDetails);
    }

    private Set<Booking> getAuthorizedBookings(UUID clientId, AppUserDetails appUserDetails) {
        Set<Booking> bookings = bookingRepository.findAllByClientIdWithFetch(clientId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Client not found");
        }

        Set<Booking> authorizedBookings;
        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            authorizedBookings = bookings.stream()
                    .filter(booking -> booking.getFlat().getProject().getOrganization().getOrgId()
                            .equals(appUserDetails.getOrgId()))
                    .collect(Collectors.toSet());
        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            Set<UUID> employeeProjectIds = employeeUserInfoRepository
                    .findByUser_UserId(UUID.fromString(appUserDetails.getUserId()))
                    .orElseThrow(() -> new NotFoundException("Employee not found"))
                    .getProjects()
                    .stream()
                    .map(Project::getProjectId)
                    .collect(Collectors.toSet());

            authorizedBookings = bookings.stream()
                    .filter(booking -> employeeProjectIds.contains(booking.getFlat().getProject().getProjectId()))
                    .collect(Collectors.toSet());
        } else {
            throw new AccessDeniedException("User not authorized for this client");
        }

        if (authorizedBookings.isEmpty()) {
            throw new AccessDeniedException("User not authorized for this client");
        }

        return authorizedBookings;
    }

    private void updateClientFields(ClientUserInfo client, ClientDetailsDTO dto) {
        if (dto.clientName() != null)
            client.setClientName(dto.clientName());
        if (dto.email() != null)
            client.setEmail(dto.email());
        if (dto.mobileNumber() != null)
            client.setMobileNumber(dto.mobileNumber());
        if (dto.dob() != null)
            client.setDob(dto.dob());
        if (dto.landlineNumber() != null)
            client.setLandlineNumber(dto.landlineNumber());
        if (dto.city() != null)
            client.setCity(dto.city());
        if (dto.address() != null)
            client.setAddress(dto.address());
        if (dto.occupation() != null)
            client.setOccupation(dto.occupation());
        if (dto.company() != null)
            client.setCompany(dto.company());
        if (dto.panNo() != null)
            client.setPanNo(dto.panNo());
        if (dto.aadharNo() != null)
            client.setAadharNo(dto.aadharNo());
    }
}
