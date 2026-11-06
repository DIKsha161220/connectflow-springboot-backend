package com.connectflow.backend.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

public record AuditLogResponse(
        Long auditId,
        Long employeeId,
        String entityType,
        Long entityId,
        String action,
        JsonNode oldValues,
        JsonNode newValues,
        String ipAddress,
        OffsetDateTime createdAt) {
}
