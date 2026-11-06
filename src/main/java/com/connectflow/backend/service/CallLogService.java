package com.connectflow.backend.service;

import com.connectflow.backend.dto.CallLogRequest;
import com.connectflow.backend.dto.CallLogResponse;
import com.connectflow.backend.security.EmployeePrincipal;

import java.net.InetAddress;
import java.util.List;

public interface CallLogService {
    CallLogResponse create(CallLogRequest request, EmployeePrincipal actor, InetAddress ipAddress);

    CallLogResponse update(Long callId, CallLogRequest request, EmployeePrincipal actor, InetAddress ipAddress);

    CallLogResponse findById(Long callId, EmployeePrincipal actor);

    List<CallLogResponse> findAll(EmployeePrincipal actor);

    List<CallLogResponse> findForInstitute(Long instituteId, EmployeePrincipal actor);
}
