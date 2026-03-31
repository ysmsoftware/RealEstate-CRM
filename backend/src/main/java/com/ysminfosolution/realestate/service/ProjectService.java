package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.ProjectBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ProjectDTO;
import com.ysminfosolution.realestate.dto.maincreationformdtos.ProjectCreationDTO;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface ProjectService {


    ResponseEntity<Project> createProject(ProjectCreationDTO newProjectDetails, AppUserDetails appUserDetails);

    ResponseEntity<List<Project>> getAllProjects();

    void hardDeleteProjectRecursive(Project project, AppUserDetails appUserDetails);

    Project getProjectById(UUID projectId);

    ResponseEntity<Project> updateProjectInfo(UUID projectId, Project incomingProject);

    ResponseEntity<String> deleteById(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<ProjectDTO> getProjectStructureById(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<List<ProjectBasicInfoDTO>> getListofBasicProjectInfo(AppUserDetails appUserDetails);

}
