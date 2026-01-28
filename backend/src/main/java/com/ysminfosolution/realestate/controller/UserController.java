package com.ysminfosolution.realestate.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.ChangeUserInfoDTO;
import com.ysminfosolution.realestate.dto.CreateNewUserRequestDTO;
import com.ysminfosolution.realestate.dto.UserResponseDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@Validated
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getLoggedInUserInfo(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        
        log.info("\n");
        log.info("Path: [GET] /users/me | Method: getLoggedInUserInfo");

        return userService.getLoggedInUserInfo(appUserDetails);
    }
    
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<UserResponseDTO>> getAllUsersForOrganization(@AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /users | Method: getAllUsersForOrganization");

        return userService.getAllUsersForOrganization(appUserDetails);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createNewUser(@Valid @RequestBody @NotNull CreateNewUserRequestDTO newEmployeeRequestDTO,
        @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /users | Method: createNewUser");
            
        return userService.createNewUser(newEmployeeRequestDTO, appUserDetails);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserInfo(@PathVariable UUID userId, @RequestBody @NotNull ChangeUserInfoDTO changeUserInfoDTO, @AuthenticationPrincipal AppUserDetails appUserDetails) {
        
        log.info("\n");
        log.info("Path: [PUT] /users/{userId} | Method: changeUserInfo");
        
        return userService.changeUserInfo(userId, changeUserInfoDTO, appUserDetails);
    }
}
