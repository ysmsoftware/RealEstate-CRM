package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.User;


public interface UserRepository extends JpaRepository<User, UUID> {

    Set<User> findAllByOrganization_OrgIdAndIsDeletedFalse(UUID orgId);

    @EntityGraph(attributePaths = "organization")
    Optional<User> findByUsernameAndIsDeletedFalseAndEnabledTrueAndOrganization_IsDeletedFalse(String username);

    @EntityGraph(attributePaths = "organization")
    Optional<User> findByOrganization_OrgIdAndUserIdAndIsDeletedFalseAndEnabledTrue(UUID orgId, UUID userId);

    boolean existsByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

}
