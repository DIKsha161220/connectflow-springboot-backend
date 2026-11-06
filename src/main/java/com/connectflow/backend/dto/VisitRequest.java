package com.connectflow.backend.dto;

import com.connectflow.backend.enums.VisitStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record VisitRequest(
        @NotNull Long instituteId,
        Long contactId,
        @NotNull OffsetDateTime visitDatetime,
        @Size(max = 255) String contactPerson,
        @Size(max = 255) String contactPhone,
        VisitStatus status,
        @Size(max = 255) String purpose,
        @Size(max = 255) String notes) {

    public VisitRequest(
            Long instituteId,
            Long contactId,
            OffsetDateTime visitDatetime,
            String contactPerson,
            String contactPhone,
            String purpose,
            String notes) {
        this(instituteId, contactId, visitDatetime, contactPerson, contactPhone, null, purpose, notes);
    }
}
