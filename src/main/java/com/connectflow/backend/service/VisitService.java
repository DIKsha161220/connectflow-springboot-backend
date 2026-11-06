package com.connectflow.backend.service;

import com.connectflow.backend.dto.VisitRequest;
import com.connectflow.backend.dto.VisitResponse;
import com.connectflow.backend.enums.VisitStatus;
import com.connectflow.backend.security.EmployeePrincipal;

import java.net.InetAddress;
import java.util.List;

public interface VisitService {
    VisitResponse create(VisitRequest request, EmployeePrincipal actor, InetAddress ipAddress);

    VisitResponse update(Long visitId, VisitRequest request, EmployeePrincipal actor, InetAddress ipAddress);

    VisitResponse findById(Long visitId, EmployeePrincipal actor);

    List<VisitResponse> findAll(EmployeePrincipal actor);

    VisitResponse updateStatus(
            Long visitId,
            VisitStatus status,
            EmployeePrincipal actor,
            InetAddress ipAddress);
}
