package com.connectflow.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record FollowUpRequest(
        @NotNull Long instituteId,
        Long relatedCallId,
        @NotNull OffsetDateTime scheduledAt,
        @Size(max = 255) String notes) {
}
