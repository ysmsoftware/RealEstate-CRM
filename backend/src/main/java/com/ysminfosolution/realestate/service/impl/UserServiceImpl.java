package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.ChangeUserInfoDTO;
import com.ysminfosolution.realestate.dto.CreateNewUserRequestDTO;
import com.ysminfosolution.realestate.dto.ProjectLeastInfoDTO;
import com.ysminfosolution.realestate.dto.UserResponseDTO;
import com.ysminfosolution.realestate.error.exception.ApiException;
import com.ysminfosolution.realestate.error.exception.ConflictException;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Admin;
import com.ysminfosolution.realestate.model.Employee;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Organization;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.model.User.Role;
import com.ysminfosolution.realestate.repository.AdminRepository;
import com.ysminfosolution.realestate.repository.EmployeeRepository;
import com.ysminfosolution.realestate.repository.OrganizationRepository;
import com.ysminfosolution.realestate.repository.ProjectRepository;
import com.ysminfosolution.realestate.repository.UserRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.UserProfileCacheService;
import com.ysminfosolution.realestate.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;

    private final ProjectResolver projectResolver;

    private final UserProfileCacheService userProfileCacheService;

    public Organization getOrganizationById(UUID orgId) {
        return organizationRepository.findByOrgIdAndIsDeletedFalse(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));
    }

    @Override
    public ResponseEntity<UserResponseDTO> getLoggedInUserInfo(AppUserDetails appUserDetails) {
        return ResponseEntity.ok(
                userProfileCacheService.getUserProfile(
                        UUID.fromString(appUserDetails.getUserId()),
                        appUserDetails.getOrgId()));
    }

    @Override
    public ResponseEntity<Set<UserResponseDTO>> getAllUsersForOrganization(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllUsersForOrganization");

        Set<User> users = userRepository.findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId());
        Set<UserResponseDTO> userResponseDTOs = new HashSet<>();

        for (User user : users) {

            UserResponseDTO userResponseDTO;

            if (user.getRole() == Role.EMPLOYEE) {
                Employee employee = employeeRepository
                        .findByUser_UserId(user.getId()).orElse(null);

                Set<ProjectLeastInfoDTO> projects = new HashSet<>();

                employee.getProjects().stream()
                        .filter(p -> !p.isDeleted())
                        .forEach(project -> {
                            projects.add(new ProjectLeastInfoDTO(project.getId(), project.getProjectName()));
                        });

                userResponseDTO = new UserResponseDTO(
                        user.getId(),
                        null,
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getMobileNumber(),
                        user.getRole(),
                        user.isEnabled(),
                        projects);

            } else {
                userResponseDTO = new UserResponseDTO(
                        user.getId(),
                        null,
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getMobileNumber(),
                        user.getRole(),
                        user.isEnabled(),
                        null);
            }

            userResponseDTOs.add(userResponseDTO);

        }

        return ResponseEntity.ok(userResponseDTOs);

    }

    @Override
    @Transactional
    public ResponseEntity<String> createNewUser(CreateNewUserRequestDTO createNewUserRequestDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createNewUser");

        Organization organization = getOrganizationById(appUserDetails.getOrgId());

        // * Check if username and email already exists
        // ! Applicable for both deleted and not deleted (Refactoring needed later)
        if (userRepository.existsByUsernameOrEmail(createNewUserRequestDTO.username(),
                createNewUserRequestDTO.email())) {
            System.out.println();
            log.error("Username or Email already exists");
            throw new ConflictException("Username or Email already exists");
        }

        Set<Project> projects = new HashSet<>();
        if (createNewUserRequestDTO.projectIds() == null || createNewUserRequestDTO.projectIds().isEmpty()) {
            projects.addAll(projectRepository.findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId()));
        } else {
            log.info("\n\nAssigning specific projects to the employee");
            for (UUID projectId : createNewUserRequestDTO.projectIds()) {
                Project project = projectResolver.resolve(projectId);
                if (project != null && !project.isDeleted()) {
                    projects.add(project);
                }
            }
            log.info("Assigned Projects Count: " + projects.size() + "\n");
        }

        User user = new User();
        user.setUsername(createNewUserRequestDTO.username());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(createNewUserRequestDTO.password()));
        user.setEmail(createNewUserRequestDTO.email());
        user.setFullName(createNewUserRequestDTO.fullName());
        user.setMobileNumber(createNewUserRequestDTO.mobileNumber());
        user.setOrganization(organization);
        user.setEnabled(true);
        user.setDeleted(false);

        // * For ADMIN Role
        if (createNewUserRequestDTO.userType().equals(User.Role.ADMIN)) {
            user.setRole(Role.ADMIN);

            user = userRepository.save(user);

            Admin admin = new Admin();
            admin.setDeleted(false);
            admin.setSuperAdmin(true);
            admin.setUser(user);

            adminRepository.save(admin);

            // * For EMPLOYEE Role
        } else if (createNewUserRequestDTO.userType().equals(User.Role.EMPLOYEE)) {
            user.setRole(User.Role.EMPLOYEE);

            user = userRepository.save(user);

            Employee employee = new Employee();
            employee.getProjects().addAll(projects);
            employee.setUser(user);
            employee.setDeleted(false);

            employeeRepository.save(employee);
        }

        return ResponseEntity.ok(user.getId().toString());
    }

    @Override
    @Transactional
    public ResponseEntity<String> changeUserInfo(UUID userId, ChangeUserInfoDTO changeUserInfoDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: changeUserInfo");

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found for id: " + userId);
        }

        // * Authorization Check: Only users from same organization can update user info
        if (!user.getOrganization().getId().equals(appUserDetails.getOrgId())) {
            throw new AccessDeniedException("Unauthorized to change user info");
        }

        if (changeUserInfoDTO.username() != null && !changeUserInfoDTO.username().isEmpty()) {
            user.setUsername(changeUserInfoDTO.username());
        }

        if (changeUserInfoDTO.password() != null && !changeUserInfoDTO.password().isEmpty()) {
            user.setPasswordHash(new BCryptPasswordEncoder().encode(changeUserInfoDTO.password()));
        }
        if (changeUserInfoDTO.email() != null && !changeUserInfoDTO.email().isEmpty()) {
            user.setEmail(changeUserInfoDTO.email());
        }
        if (changeUserInfoDTO.fullName() != null && !changeUserInfoDTO.fullName().isEmpty()) {
            user.setFullName(changeUserInfoDTO.fullName());
        }
        if (changeUserInfoDTO.mobileNumber() != null && !changeUserInfoDTO.mobileNumber().isEmpty()) {
            user.setMobileNumber(changeUserInfoDTO.mobileNumber());
        }

        if (changeUserInfoDTO.userType() != null) {
            user.setRole(changeUserInfoDTO.userType());
        }

        if (changeUserInfoDTO.userType().equals(User.Role.ADMIN)) {
            if (!adminRepository.existsByUser_Id(user.getId())) {
                Admin admin = new Admin();
                admin.setDeleted(false);
                admin.setUser(user);

                if (changeUserInfoDTO.isEnabled() != null) {

                    if (admin.isSuperAdmin() && !changeUserInfoDTO.isEnabled()) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot disable super admin");
                    }

                    user.setEnabled(changeUserInfoDTO.isEnabled());
                }

                adminRepository.save(admin);
            }
        } else if (changeUserInfoDTO.userType().equals(User.Role.EMPLOYEE)) {
            if (!employeeRepository.existsByUser_UserId(user.getId())) {
                Employee employee = new Employee();

                employee.setProjects(
                        projectRepository.findAllByProjectIdInAndIsDeletedFalse(changeUserInfoDTO.projectIds()));
                employee.setUser(user);
                employee.setDeleted(false);

                employeeRepository.save(employee);

            }

        }

        userRepository.save(user);

        return ResponseEntity.ok("User info updated successfully");

    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteById(UUID userId, AppUserDetails appUserDetails) {
        log.info("\n");
        log.info("Method: deleteById");

        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.isDeleted()) {
            throw new NotFoundException("User not found for id: " + userId);
        }

        if (!user.getOrganization().getId().equals(appUserDetails.getOrgId())) {
            throw new AccessDeniedException("Unauthorized to delete user");
        }

        user.setDeleted(true);
        user.setEnabled(false);
        userRepository.save(user);

        employeeRepository.findByUser_UserId(userId).ifPresent(employee -> {
            employee.setDeleted(true);
            employeeRepository.save(employee);
        });

        adminRepository.findByUser_Id(userId).ifPresent(admin -> {
            if (admin.isSuperAdmin()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete super admin user");
            }
            admin.setDeleted(true);
            adminRepository.save(admin);
        });

        return ResponseEntity.ok("User deleted successfully");
    }
}
