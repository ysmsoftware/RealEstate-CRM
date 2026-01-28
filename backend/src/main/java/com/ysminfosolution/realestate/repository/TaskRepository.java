package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Task;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    boolean existsByFollowUp(FollowUp followUp);

    @Query("""
                SELECT t FROM Task t
                JOIN FETCH t.followUp f
                JOIN FETCH f.enquiry e
                JOIN FETCH e.project p
                WHERE p IN :projects AND f.isDeleted = false
            """)
    Set<Task> findAllByProjectsWithFetch(@Param("projects") Set<Project> projects);

    void deleteByFollowUp_Enquiry(Enquiry enquiry);

}
