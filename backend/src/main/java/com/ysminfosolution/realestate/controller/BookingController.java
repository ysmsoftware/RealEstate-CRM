package com.ysminfosolution.realestate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.BookingDTO;
import com.ysminfosolution.realestate.dto.NewBookingDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.BookingService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@Validated
@RequestMapping("/bookings")
@Transactional
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("")
    public ResponseEntity<BookingDTO> createNewBooking(@RequestBody @NotNull NewBookingDTO newBookingDTO, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /bookings | Method: createNewBooking");

        return bookingService.createNewBooking(newBookingDTO, appUserDetails);
    }
        
}
