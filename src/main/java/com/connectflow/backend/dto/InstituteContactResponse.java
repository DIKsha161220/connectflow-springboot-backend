package com.connectflow.backend.dto;

import java.time.OffsetDateTime;

public record InstituteContactResponse(
        Long contactId,
        Long instituteId,
        String contactName,
        String designation,
        String phone,
        String email,
        Boolean primary,
        String notes,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
