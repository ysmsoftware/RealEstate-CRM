package com.ysminfosolution.realestate.resolver;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ysminfosolution.realestate.context.ProjectContext;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectResolver {

    private final ProjectRepository projectRepository;
    private final ProjectContext projectContext;

    public Project resolve(UUID projectId) {

        // ✅ Already loaded in this request
        if (projectContext.getProject() != null) {
            return projectContext.getProject();
        }

        // ✅ Load from cache or DB
        Project project = resolveFromCacheOrDb(projectId);

        // ✅ Cache for rest of request
        projectContext.setProject(project);
        return project;
    }

    private Project resolveFromCacheOrDb(UUID projectId) {
        return projectRepository
                .findWithOrganizationByProjectId(projectId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() ->
                        new NotFoundException("Project not found for id: " + projectId));
    }
}
