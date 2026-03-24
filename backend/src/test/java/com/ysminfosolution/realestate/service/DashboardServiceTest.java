package com.ysminfosolution.realestate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.DashboardProjectResponseDTO;
import com.ysminfosolution.realestate.dto.DashboardResponseDTO;
import com.ysminfosolution.realestate.dto.EnquiryBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ProjectBasicInfoDTO;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.FlatRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private EnquiryService enquiryService;

    @Mock
    private FlatRepository flatRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardDataUsesProjectScopedPropertyCounts() {
        UUID projectOneId = UUID.randomUUID();
        UUID projectTwoId = UUID.randomUUID();
        UUID projectThreeId = UUID.randomUUID();

        Set<ProjectBasicInfoDTO> projects = Set.of(
                project(projectOneId, "Project One"),
                project(projectTwoId, "Project Two"),
                project(projectThreeId, "Project Three"));

        Set<EnquiryBasicInfoDTO> enquiries = Set.of(
                enquiry(projectOneId, Enquiry.Status.BOOKED),
                enquiry(projectTwoId, Enquiry.Status.ONGOING),
                enquiry(projectTwoId, Enquiry.Status.CANCELLED));

        when(projectService.getListofBasicProjectInfo(null)).thenReturn(ResponseEntity.ok(projects));
        when(enquiryService.getListOfEnquiryBasicInfo(null)).thenReturn(ResponseEntity.ok(enquiries));
        when(flatRepository.countActiveFlatsByProjectIds(org.mockito.ArgumentMatchers.anyList())).thenReturn(
                java.util.List.of(
                        count(projectOneId, 10L),
                        count(projectTwoId, 4L)));

        DashboardResponseDTO response = dashboardService.getDashboardData(null);
        Map<UUID, DashboardProjectResponseDTO> projectsById = response.projects().stream()
                .collect(Collectors.toMap(DashboardProjectResponseDTO::id, Function.identity()));

        assertEquals(3, response.totalProjects());
        assertEquals(14L, response.totalProperties());
        assertEquals(1L, response.propertiesBooked());
        assertEquals(13, response.propertiesAvailable());
        assertEquals(3, response.totalEnquiries());
        assertEquals(1, response.cancelledEnquiries());

        assertEquals(10L, projectsById.get(projectOneId).totalProperties());
        assertEquals(1L, projectsById.get(projectOneId).propertiesBooked());
        assertEquals(9, projectsById.get(projectOneId).propertiesAvailable());

        assertEquals(4L, projectsById.get(projectTwoId).totalProperties());
        assertEquals(0L, projectsById.get(projectTwoId).propertiesBooked());
        assertEquals(4, projectsById.get(projectTwoId).propertiesAvailable());
        assertEquals(2, projectsById.get(projectTwoId).totalEnquiries());
        assertEquals(1, projectsById.get(projectTwoId).cancelledEnquiries());

        assertEquals(0L, projectsById.get(projectThreeId).totalProperties());
        assertEquals(0L, projectsById.get(projectThreeId).propertiesBooked());
        assertEquals(0, projectsById.get(projectThreeId).propertiesAvailable());
        assertEquals(0, projectsById.get(projectThreeId).totalEnquiries());
        assertEquals(0, projectsById.get(projectThreeId).cancelledEnquiries());
    }

    private static ProjectBasicInfoDTO project(UUID projectId, String projectName) {
        return new ProjectBasicInfoDTO(
                projectId,
                projectName,
                "Address",
                "400001",
                Project.Status.IN_PROGRESS,
                (short) 50,
                LocalDate.now(),
                LocalDate.now().plusMonths(6));
    }

    private static EnquiryBasicInfoDTO enquiry(UUID projectId, Enquiry.Status status) {
        return new EnquiryBasicInfoDTO(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "Client",
                projectId,
                "Project",
                "1000000",
                status);
    }

    private static FlatRepository.ProjectFlatCount count(UUID projectId, Long totalProperties) {
        return new FlatRepository.ProjectFlatCount() {
            @Override
            public UUID getProjectId() {
                return projectId;
            }

            @Override
            public Long getTotalProperties() {
                return totalProperties;
            }
        };
    }
}
