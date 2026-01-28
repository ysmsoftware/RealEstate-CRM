package com.ysminfosolution.realestate.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@Validated
@RequestMapping("")
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    

    @GetMapping("/status")
    public ResponseEntity<String> appicationStatus() {

        log.info("\n");
        log.info("Path: [GET] /status | Method: appicationStatus");

        return ResponseEntity.ok("Application up and running...");
    }

    

}
