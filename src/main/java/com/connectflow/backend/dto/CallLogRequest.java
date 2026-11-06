package com.connectflow.backend.dto;

import com.connectflow.backend.enums.CallOutcome;
import com.connectflow.backend.enums.CallType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CallLogRequest(
        @NotNull Long instituteId,
        Long contactId,
        @NotNull CallType callType,
        @NotNull OffsetDateTime callDatetime,
        @Size(max = 255) String contactPerson,
        @Size(max = 255) String contactPhone,
        @NotNull CallOutcome callOutcome,
        OffsetDateTime nextFollowUpAt,
        @Size(max = 255) String notes) {
}
