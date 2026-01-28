package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.dto.FollowUpNodeRequestDTO;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.FollowUpNode;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface FollowUpNodeService {

    boolean createFirstNodeForFollowUp(FollowUp followUp, AppUserDetails appUserDetails);

    Set<FollowUpNode> getAllByFollowUpId(UUID followUpId);

    Set<FollowUpNode> getAllByFollowUpIds(Set<UUID> followUpIds);

    void createNodeForFollowUp(FollowUp followUp, FollowUpNodeRequestDTO nodeRequestDTO, AppUserDetails appUserDetails);

    void deleteAllNodesForFollowUp(UUID followUpId);

}
