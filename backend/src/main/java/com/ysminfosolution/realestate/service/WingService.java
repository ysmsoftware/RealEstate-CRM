package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.maincreationformdtos.WingCreationDTO;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Wing;

public interface WingService {

    Boolean createWingsForProject(Project savedProject, List<WingCreationDTO> wings);

    void hardDeleteWingsRecursiveByProjectId(UUID projectId);

    ResponseEntity<Wing> updateWing(UUID wingId, WingCreationDTO wingCreationDTO);

    ResponseEntity<Wing> createWingForProjectId(UUID projectId, WingCreationDTO wingCreationDTO);

    Boolean deleteWingRecursiveByWingtId(UUID wingId);

    void deleteWingsRecursiveByProjectId(UUID projectId);

    Set<Wing> getAllWingsForProject(UUID projectId);

    Set<Wing> getFullStructureByProjectId(UUID projectId);
    
}
