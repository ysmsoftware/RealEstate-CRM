package com.ysminfosolution.realestate.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import com.ysminfosolution.realestate.model.Project;

@Component
@RequestScope
public class ProjectContext {

    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
