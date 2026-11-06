package com.connectflow.backend.dto;

import com.connectflow.backend.enums.FollowUpStatus;

import java.time.OffsetDateTime;

public record FollowUpResponse(
        Long followUpId,
        Long instituteId,
        Long employeeId,
        Long relatedCallId,
        OffsetDateTime scheduledAt,
        FollowUpStatus followUpStatus,
        String notes,
        OffsetDateTime completedAt,
        Long completedBy,
        Long createdBy,
        Long updatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
