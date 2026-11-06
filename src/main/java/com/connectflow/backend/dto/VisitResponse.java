package com.connectflow.backend.dto;

import com.connectflow.backend.enums.VisitStatus;

import java.time.OffsetDateTime;

public record VisitResponse(
        Long visitId,
        Long instituteId,
        Long employeeId,
        Long contactId,
        OffsetDateTime visitDatetime,
        String contactPerson,
        String contactPhone,
        VisitStatus visitStatus,
        String purpose,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
