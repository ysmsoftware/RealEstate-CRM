package com.ysminfosolution.realestate.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

@Service
public class ProjectAuthorizationService {

   
    public boolean isAuthorized(AppUserDetails user, Project project) {

        if (user == null) {
            return true;
        }

        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }

        return project.getOrganization()
                      .getOrgId()
                      .equals(user.getOrgId());
    }

    public void checkProjectAccess(AppUserDetails user, Project project) {
        if (!isAuthorized(user, project)) {
            throw new AccessDeniedException("User not authorized for this project");
        }
    }
}
