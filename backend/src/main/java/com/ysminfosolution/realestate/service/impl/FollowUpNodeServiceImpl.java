package com.ysminfosolution.realestate.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.FollowUpNodeRequestDTO;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.FollowUpNode;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.FollowUpNodeRepository;
import com.ysminfosolution.realestate.repository.UserRepository;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.FollowUpNodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FollowUpNodeServiceImpl implements FollowUpNodeService {

    private final FollowUpNodeRepository followUpNodeRepository;
    private final UserRepository userRepository;

    @Override
    @SuppressWarnings("null")
    public boolean createFirstNodeForFollowUp(FollowUp followUp, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createFirstNodeForFollowUp");

        User user = userRepository.findById(UUID.fromString(appUserDetails.getUserId())).orElse(null);

        FollowUpNode followUpNode = new FollowUpNode();

        followUpNode.setBody("First FollowUp");
        followUpNode.setFollowUpDateTime(LocalDateTime.now());
        followUpNode.setFollowUp(followUp);
        followUpNode.setTag("First FollowUp");
        followUpNode.setUser(user);
        followUpNode.setDeleted(false);

        try {
            followUpNodeRepository.save(followUpNode);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Set<FollowUpNode> getAllByFollowUpId(UUID followUpId) {

        log.info("\n");
        log.info("Method: getAllByFollowUpId");

        return followUpNodeRepository.findAllByFollowUp_FollowUpId(followUpId);
    }

    @Override
    public Set<FollowUpNode> getAllByFollowUpIds(Set<UUID> followUpIds) {
        if (followUpIds == null || followUpIds.isEmpty()) {
            return Set.of();
        }
        return followUpNodeRepository.findAllByFollowUpIdsWithUser(followUpIds);
    }

    @Override
    @SuppressWarnings("null")
    public void createNodeForFollowUp(FollowUp followUp, FollowUpNodeRequestDTO nodeRequestDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createNodeForFollowUp");

        User user = userRepository.findById(UUID.fromString(appUserDetails.getUserId())).orElse(null);

        FollowUpNode followUpNode = new FollowUpNode();
        followUpNode.setBody(nodeRequestDTO.body());
        followUpNode.setFollowUpDateTime(LocalDateTime.now());
        followUpNode.setTag(nodeRequestDTO.tag());
        followUpNode.setFollowUp(followUp);
        followUpNode.setUser(user);
        followUpNode.setDeleted(false);

        followUpNodeRepository.save(followUpNode);
    }

    @Override
    @SuppressWarnings("null")
    public void deleteAllNodesForFollowUp(UUID followUpId) {

        log.info("\n");
        log.info("Method: getAllByFollowUpId");

        Set<FollowUpNode> followUpNodes = followUpNodeRepository.findAllByFollowUp_FollowUpId(followUpId);
        for (FollowUpNode followUpNode : followUpNodes) {
            
            followUpNode.setDeleted(true);
        }
        followUpNodeRepository.saveAll(followUpNodes);
    }

}
