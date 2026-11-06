package com.connectflow.backend.service;

import com.connectflow.backend.dto.AuditLogResponse;
import com.connectflow.backend.entity.Employee;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.InetAddress;
import java.util.List;

public interface AuditLogService {
    List<AuditLogResponse> findAll();

    AuditLogResponse findById(Long auditId);

    void record(
            Employee actor,
            String entityType,
            Long entityId,
            String action,
            JsonNode oldValues,
            JsonNode newValues,
            InetAddress ipAddress);
}
