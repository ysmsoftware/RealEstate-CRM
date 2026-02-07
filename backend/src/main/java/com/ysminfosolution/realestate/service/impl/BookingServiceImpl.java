package com.ysminfosolution.realestate.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.BookingDTO;
import com.ysminfosolution.realestate.dto.NewBookingDTO;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Booking;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.repository.*;
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

    // * Repository
    private final ClientUserInfoRepository clientRepository;
    private final BookingRepository bookingRepository;
    private final FlatRepository flatRepository;
    private final EnquiryRepository enquiryRepository;

    @Override
    @SuppressWarnings("null")
    public ResponseEntity<BookingDTO> createNewBooking(NewBookingDTO newBookingDTO, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createNewBooking");


        Enquiry enquiry = newBookingDTO.enquiryId() != null
                ? enquiryRepository.findById(newBookingDTO.enquiryId()).orElse(null)
                : null;

        Flat flat = flatRepository.findByPropertyIdAndIsDeletedFalse(newBookingDTO.propertyId()).orElseThrow(() -> new NotFoundException("Flat not found"));

        Project project = projectResolver.resolve(flat.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        ClientUserInfo clientUserInfo;

        // ~ Indicating that an enquiry was successfully completed and client was
        // ~ forwarded for booking Therefore using the enquiry's Client
        if (enquiry != null) {
            enquiry.setStatus(Status.BOOKED);
            enquiry.setDeleted(true);
            enquiryRepository.save(enquiry);

            // ? Setting extra info available from enquiry
            clientUserInfo = enquiry.getClient();
            clientUserInfo.setAadharNo(newBookingDTO.aadharNo());
            clientUserInfo.setDob(newBookingDTO.dob());
            clientUserInfo.setPanNo(newBookingDTO.panNo());

        } else if (newBookingDTO.clientId() == null) {
            // ~ No enquiry was Involved And No Client was chosen Therefore creating new
            // Client
            clientUserInfo = new ClientUserInfo();
            clientUserInfo.setAadharNo(newBookingDTO.aadharNo());
            clientUserInfo.setDob(newBookingDTO.dob());
            clientUserInfo.setPanNo(newBookingDTO.panNo());
            clientUserInfo.setClientName(newBookingDTO.clientName());
        } else {
            clientUserInfo = clientRepository.findById(newBookingDTO.clientId()).orElse(null);
            clientUserInfo.setAadharNo(newBookingDTO.aadharNo());
            clientUserInfo.setDob(newBookingDTO.dob());
            clientUserInfo.setPanNo(newBookingDTO.panNo());
        }

        clientUserInfo = clientRepository.save(clientUserInfo);

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

        BookingDTO bookingDTO = new BookingDTO(
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
                booking.getRemark());

        return ResponseEntity.ok(bookingDTO);
    }

    
}
