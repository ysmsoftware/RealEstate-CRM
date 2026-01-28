package com.ysminfosolution.realestate.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.maincreationformdtos.WingCreationDTO;
import com.ysminfosolution.realestate.model.Wing;
import com.ysminfosolution.realestate.service.WingService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@Validated
@RequestMapping("/wings")
@Slf4j
@Transactional
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class WingController {

    private final WingService wingService;

    @PostMapping("/{projectId}")
    public ResponseEntity<Wing> createWingForProjectId(@PathVariable @NotNull UUID projectId, @RequestBody @NotNull @Valid WingCreationDTO wingCreationDTO) {

        log.info("\n");
        log.info("Path: [POST] /wings/{projectId} | Method: createWingForProjectId");

        return wingService.createWingForProjectId(projectId, wingCreationDTO);
    }
    

    @PutMapping("/{wingId}")
    public ResponseEntity<Wing> updateWing(@PathVariable @NotNull UUID wingId, @RequestBody @NotNull @Valid WingCreationDTO wingCreationDTO) {

        log.info("\n");
        log.info("Path: [PUT] /wings/{wingId} | Method: updateWing");

        return wingService.updateWing(wingId, wingCreationDTO);
    }


    @DeleteMapping("/{wingId}")
    public ResponseEntity<String> deleteWing(@PathVariable @Valid UUID wingId) {

        log.info("\n");
        log.info("Path: [DELETE] /wings/{wingId} | Method: deleteWing");

        if(!wingService.deleteWingRecursiveByWingtId(wingId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Wing Deleted Successfully");

    }

}
