package com.ysminfosolution.realestate.service;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.BookingDTO;
import com.ysminfosolution.realestate.dto.NewBookingDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface BookingService {


    ResponseEntity<BookingDTO> createNewBooking(NewBookingDTO newBookingDTO, AppUserDetails appUserDetails);

}
