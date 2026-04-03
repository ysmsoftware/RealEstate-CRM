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
import com.ysminfosolution.realestate.model.Client;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.BookingRepository;
import com.ysminfosolution.realestate.repository.ClientRepository;
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

    private final ClientRepository clientRepository;
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

        Flat flat = flatRepository.findById(newBookingDTO.propertyId())
                .orElseThrow(() -> new NotFoundException("Flat not found"));

        Project project = projectResolver.resolve(flat.getProject().getId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        if (enquiry != null) {
            validateEnquiryForBooking(enquiry, flat);
        }

        Client client = enquiry != null
                ? buildClientFromEnquiry(enquiry, newBookingDTO)
                : buildClientFromBooking(newBookingDTO);

        client = clientRepository.save(client);

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
        booking.setClient(client);
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
                booking.getId(),
                booking.getClient().getId(),
                booking.getEnquiry() == null ? null : booking.getEnquiry().getId(),
                booking.getFlat().getId(),
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

        if (!enquiry.getProject().getId().equals(flat.getProject().getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Enquiry and booking property must belong to the same project");
        }
    }

    private Client buildClientFromEnquiry(Enquiry enquiry, NewBookingDTO newBookingDTO) {
        Client client = new Client();
        client.setClientName(enquiry.getLeadName());
        client.setMobileNumber(enquiry.getLeadMobileNumber());
        client.setLandlineNumber(enquiry.getLeadLandlineNumber());
        client.setEmail(enquiry.getLeadEmail());
        client.setCity(enquiry.getLeadCity());
        client.setAddress(enquiry.getLeadAddress());
        client.setOccupation(enquiry.getLeadOccupation());
        client.setCompany(enquiry.getLeadCompany());
        applyBookingKyc(client, newBookingDTO);
        client.setDeleted(false);
        return client;
    }

    private Client buildClientFromBooking(NewBookingDTO newBookingDTO) {
        validateDirectClientDetails(newBookingDTO);

        Client client = new Client();
        client.setClientName(newBookingDTO.clientName());
        client.setMobileNumber(newBookingDTO.mobileNumber());
        client.setLandlineNumber(newBookingDTO.landlineNumber());
        client.setEmail(newBookingDTO.email());
        client.setCity(newBookingDTO.city());
        client.setAddress(newBookingDTO.address());
        client.setOccupation(newBookingDTO.occupation());
        client.setCompany(newBookingDTO.company());
        applyBookingKyc(client, newBookingDTO);
        client.setDeleted(false);
        return client;
    }

    private void applyBookingKyc(Client client, NewBookingDTO newBookingDTO) {
        client.setDob(newBookingDTO.dob());
        client.setPanNo(newBookingDTO.panNo());
        client.setAadharNo(newBookingDTO.aadharNo());
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
