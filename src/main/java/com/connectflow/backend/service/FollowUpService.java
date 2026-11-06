package com.connectflow.backend.service;

import com.connectflow.backend.dto.FollowUpRequest;
import com.connectflow.backend.dto.FollowUpResponse;

import java.net.InetAddress;
import java.util.List;

public interface FollowUpService {
    List<FollowUpResponse> findAll();

    FollowUpResponse findById(Long followUpId);

    FollowUpResponse create(FollowUpRequest request, Long actorId, InetAddress ipAddress);

    FollowUpResponse complete(Long followUpId, Long actorId, InetAddress ipAddress);
}
