package com.ysminfosolution.realestate.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ysminfosolution.realestate.dto.ProjectBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ProjectDTO;
import com.ysminfosolution.realestate.dto.maincreationformdtos.ProjectCreationDTO;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Project.Status;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ProjectService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;





@RestController
@Validated
@RequestMapping("/projects")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<Project>> getAllProjects() {

        log.info("\n");
        log.info("Path: [GET] /projects | Method: getAllProjects");

        return projectService.getAllProjects();
    }
    
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectStructureById(@PathVariable @NotNull UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /projects/{projectId} | Method: getProjectStructureById");

        return projectService.getProjectStructureById(projectId, appUserDetails);
    }
    
    @GetMapping("/basicinfolist")
    public ResponseEntity<Set<ProjectBasicInfoDTO>> getListofBasicProjectInfo(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        
        log.info("\n");
        log.info("Path: [GET] /projects/basicinfolist | Method: getListofBasicProjectInfo");

        return projectService.getListofBasicProjectInfo(appUserDetails);

    }
    

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> createProject(@Valid @ModelAttribute @NotNull ProjectCreationDTO newProjectDetails, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /projects | Method: createProject");

        log.info("New Project Data Recieved, processing now...");

        return projectService.createProject(newProjectDetails, appUserDetails);
    }

    @PutMapping("/complete/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeProjectStatus(@PathVariable @NotNull UUID projectId, @RequestParam @NotNull Status status) {

        log.info("\n");
        log.info("Path: [PUT] /projects/complete/{projectId} | Method: changeProjectStatus");

        return projectService.changeProjectStatus(projectId, status);
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Project> updateProjectInfo(@PathVariable @NotNull UUID projectId, @RequestBody @NotNull Project incomingProject) {

        log.info("\n");
        log.info("Path: [PUT] /projects/{projectId} | Method: updateProjectInfo");

        return projectService.updateProjectInfo(projectId, incomingProject);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteById(@PathVariable @NotNull UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [DELETE] /projects/{projectId} | Method: deleteById");

        return projectService.deleteById(projectId, appUserDetails);
    }

}
