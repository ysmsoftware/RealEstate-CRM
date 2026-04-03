package com.ysminfosolution.realestate.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.BookingDTO;
import com.ysminfosolution.realestate.dto.NewBookingDTO;
import com.ysminfosolution.realestate.error.exception.ApiException;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Booking;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.BookingRepository;
import com.ysminfosolution.realestate.repository.ClientUserInfoRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FlatRepository;
import com.ysminfosolution.realestate.repository.TaskRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.BookingService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ProjectResolver projectResolver;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final ClientUserInfoRepository clientRepository;
    private final BookingRepository bookingRepository;
    private final FlatRepository flatRepository;
    private final EnquiryRepository enquiryRepository;
    private final TaskRepository taskRepository;

    @Override
    public ResponseEntity<BookingDTO> createNewBooking(NewBookingDTO newBookingDTO, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createNewBooking");

        Enquiry enquiry = newBookingDTO.enquiryId() == null ? null
                : enquiryRepository.findById(newBookingDTO.enquiryId())
                        .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        Flat flat = flatRepository.findByPropertyIdAndIsDeletedFalse(newBookingDTO.propertyId())
                .orElseThrow(() -> new NotFoundException("Flat not found"));

        Project project = projectResolver.resolve(flat.getProject().getProjectId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        if (enquiry != null) {
            validateEnquiryForBooking(enquiry, flat);
        }

        ClientUserInfo clientUserInfo = enquiry != null
                ? buildClientFromEnquiry(enquiry, newBookingDTO)
                : buildClientFromBooking(newBookingDTO);

        clientUserInfo = clientRepository.save(clientUserInfo);

        if (enquiry != null) {
            enquiry.setStatus(Status.BOOKED);
            taskRepository.deleteByFollowUp_Enquiry(enquiry);
            enquiryRepository.save(enquiry);
        }

        Booking booking = new Booking();
        booking.setAgreementAmount(newBookingDTO.agreementAmount());
        booking.setBookingAmount(newBookingDTO.bookingAmount());
        booking.setBookingDate(newBookingDTO.bookingDate());
        booking.setChequeDate(newBookingDTO.chequeDate());
        booking.setChequeNo(newBookingDTO.chequeNo());
        booking.setClient(clientUserInfo);
        booking.setEnquiry(enquiry);
        booking.setFlat(flat);
        booking.setRate(newBookingDTO.rate());
        booking.setInfraAmount(newBookingDTO.infraAmount());
        booking.setGstAmount(newBookingDTO.gstAmount());
        booking.setGstPercentage(newBookingDTO.gstPercentage());
        booking.setTotalAmount(newBookingDTO.totalAmount());
        booking.setCancelled(false);
        booking.setRegistered(false);
        booking.setDeleted(false);

        booking = bookingRepository.save(booking);

        return ResponseEntity.ok(new BookingDTO(
                booking.getBookingId(),
                booking.getClient().getClientId(),
                booking.getEnquiry() == null ? null : booking.getEnquiry().getEnquiryId(),
                booking.getFlat().getPropertyId(),
                booking.getAgreementAmount(),
                booking.getBookingAmount(),
                booking.getBookingDate(),
                booking.getChequeDate(),
                booking.getChequeNo(),
                booking.getGstPercentage(),
                booking.getGstAmount(),
                booking.getInfraAmount(),
                booking.getTotalAmount(),
                booking.getRate(),
                booking.isRegistered(),
                booking.getRegNo(),
                booking.getRegDate(),
                booking.isCancelled(),
                booking.getRemark()));
    }

    private void validateEnquiryForBooking(Enquiry enquiry, Flat flat) {
        if (enquiry.isDeleted()) {
            throw new NotFoundException("Enquiry not found");
        }

        if (enquiry.getStatus() == Status.CANCELLED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot create a booking for a cancelled enquiry");
        }

        if (enquiry.getStatus() == Status.BOOKED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Booking already exists for this enquiry");
        }

        if (!enquiry.getProject().getProjectId().equals(flat.getProject().getProjectId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Enquiry and booking property must belong to the same project");
        }
    }

    private ClientUserInfo buildClientFromEnquiry(Enquiry enquiry, NewBookingDTO newBookingDTO) {
        ClientUserInfo clientUserInfo = new ClientUserInfo();
        clientUserInfo.setClientName(enquiry.getLeadName());
        clientUserInfo.setMobileNumber(enquiry.getLeadMobileNumber());
        clientUserInfo.setLandlineNumber(enquiry.getLeadLandlineNumber());
        clientUserInfo.setEmail(enquiry.getLeadEmail());
        clientUserInfo.setCity(enquiry.getLeadCity());
        clientUserInfo.setAddress(enquiry.getLeadAddress());
        clientUserInfo.setOccupation(enquiry.getLeadOccupation());
        clientUserInfo.setCompany(enquiry.getLeadCompany());
        applyBookingKyc(clientUserInfo, newBookingDTO);
        clientUserInfo.setDeleted(false);
        return clientUserInfo;
    }

    private ClientUserInfo buildClientFromBooking(NewBookingDTO newBookingDTO) {
        validateDirectClientDetails(newBookingDTO);

        ClientUserInfo clientUserInfo = new ClientUserInfo();
        clientUserInfo.setClientName(newBookingDTO.clientName());
        clientUserInfo.setMobileNumber(newBookingDTO.mobileNumber());
        clientUserInfo.setLandlineNumber(newBookingDTO.landlineNumber());
        clientUserInfo.setEmail(newBookingDTO.email());
        clientUserInfo.setCity(newBookingDTO.city());
        clientUserInfo.setAddress(newBookingDTO.address());
        clientUserInfo.setOccupation(newBookingDTO.occupation());
        clientUserInfo.setCompany(newBookingDTO.company());
        applyBookingKyc(clientUserInfo, newBookingDTO);
        clientUserInfo.setDeleted(false);
        return clientUserInfo;
    }

    private void applyBookingKyc(ClientUserInfo clientUserInfo, NewBookingDTO newBookingDTO) {
        clientUserInfo.setDob(newBookingDTO.dob());
        clientUserInfo.setPanNo(newBookingDTO.panNo());
        clientUserInfo.setAadharNo(newBookingDTO.aadharNo());
    }

    private void validateDirectClientDetails(NewBookingDTO newBookingDTO) {
        if (isBlank(newBookingDTO.clientName()) || isBlank(newBookingDTO.mobileNumber()) || isBlank(newBookingDTO.email())
                || isBlank(newBookingDTO.city()) || isBlank(newBookingDTO.address())
                || isBlank(newBookingDTO.occupation()) || isBlank(newBookingDTO.company())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Client name, mobile number, email, city, address, occupation and company are required when booking without an enquiry");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
