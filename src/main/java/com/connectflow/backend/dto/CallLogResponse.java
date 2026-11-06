package com.connectflow.backend.dto;

import com.connectflow.backend.enums.CallOutcome;
import com.connectflow.backend.enums.CallType;

import java.time.OffsetDateTime;

public record CallLogResponse(
        Long callId,
        Long instituteId,
        Long employeeId,
        Long contactId,
        CallType callType,
        OffsetDateTime callDatetime,
        String contactPerson,
        String contactPhone,
        CallOutcome callOutcome,
        OffsetDateTime nextFollowUpAt,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
