package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.ChangeUserInfoDTO;
import com.ysminfosolution.realestate.dto.CreateNewUserRequestDTO;
import com.ysminfosolution.realestate.dto.UserResponseDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;


public interface UserService {

    ResponseEntity<UserResponseDTO> getLoggedInUserInfo(AppUserDetails appUserDetails);

    ResponseEntity<Set<UserResponseDTO>> getAllUsersForOrganization(AppUserDetails appUserDetails);

    ResponseEntity<String> createNewUser(CreateNewUserRequestDTO createNewUserRequestDTO,
            AppUserDetails appUserDetails);

    ResponseEntity<String> changeUserInfo(UUID userId, ChangeUserInfoDTO changeUserInfoDTO, AppUserDetails appUserDetails);

    ResponseEntity<String> deleteById(UUID userId, AppUserDetails appUserDetails);


}
