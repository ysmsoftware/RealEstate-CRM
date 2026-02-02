package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ysminfosolution.realestate.dto.DashboardProjectDetailedResponseDTO;
import com.ysminfosolution.realestate.dto.DashboardProjectResponseDTO;
import com.ysminfosolution.realestate.dto.DashboardResponseDTO;
import com.ysminfosolution.realestate.dto.EnquiryBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ProjectBasicInfoDTO;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.repository.FlatRepository;
import com.ysminfosolution.realestate.security.AppUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectService projectService;
    private final EnquiryService enquiryService;
    private final FlatRepository flatRepository;

    public DashboardResponseDTO getDashboardData(AppUserDetails appUserDetails) {

        Set<ProjectBasicInfoDTO> projects = projectService.getListofBasicProjectInfo(appUserDetails).getBody();

        Set<EnquiryBasicInfoDTO> enquiries = enquiryService.getListOfEnquiryBasicInfo(appUserDetails).getBody();

        if (projects == null || enquiries == null) {
            return new DashboardResponseDTO(0, 0, 0, 0, 0, 0, Set.of());
        }

        // -----------------------------------------
        // Project IDs
        // -----------------------------------------
        List<UUID> projectIds = projects.stream()
                .map(ProjectBasicInfoDTO::projectId)
                .toList();

        long totalProperties = flatRepository.countByProject_ProjectIdInAndIsDeletedFalse(projectIds);

        // -----------------------------------------
        // Global enquiry stats
        // -----------------------------------------
        long propertiesBooked = enquiries.stream()
                .filter(e -> e.status() == Enquiry.Status.BOOKED)
                .count();

        int cancelledEnquiries = (int) enquiries.stream()
                .filter(e -> e.status() == Enquiry.Status.CANCELLED)
                .count();

        int totalEnquiries = enquiries.size();
        int totalProjects = projects.size();
        int propertiesAvailable = (int) (totalProperties - propertiesBooked);

        // -----------------------------------------
        // Group enquiries by project
        // -----------------------------------------
        Map<UUID, List<EnquiryBasicInfoDTO>> enquiriesByProject = enquiries.stream()
                .collect(Collectors.groupingBy(EnquiryBasicInfoDTO::projectId));

        // -----------------------------------------
        // Per-project dashboard data
        // -----------------------------------------
        Set<DashboardProjectResponseDTO> projectList = projects.stream()
                .map(project -> {

                    List<EnquiryBasicInfoDTO> projectEnquiries = enquiriesByProject.getOrDefault(project.projectId(),
                            List.of());

                    long projectBooked = projectEnquiries.stream()
                            .filter(e -> e.status() == Enquiry.Status.BOOKED)
                            .count();

                    int projectCancelled = (int) projectEnquiries.stream()
                            .filter(e -> e.status() == Enquiry.Status.CANCELLED)
                            .count();

                    long projectTotalProperties = totalProperties; // adjust if you later support per-project counts
                    int projectAvailable = (int) (projectTotalProperties - projectBooked);

                    return new DashboardProjectResponseDTO(
                            project.projectId(),
                            project.projectName(),
                            projectTotalProperties,
                            projectBooked,
                            projectAvailable,
                            projectEnquiries.size(),
                            projectCancelled);
                })
                .collect(Collectors.toSet());

        // -----------------------------------------
        // Final response
        // -----------------------------------------
        return new DashboardResponseDTO(
                totalProjects,
                totalProperties,
                propertiesBooked,
                propertiesAvailable,
                totalEnquiries,
                cancelledEnquiries,
                projectList);
    }

    public DashboardProjectDetailedResponseDTO getDashboardDataForProject(UUID projectId, AppUserDetails appUserDetails) {
        
        return new DashboardProjectDetailedResponseDTO();

    }

}
