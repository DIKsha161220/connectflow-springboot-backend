package com.connectflow.backend.dto;

import java.time.OffsetDateTime;

public record RoleResponse(
        Long roleId,
        String roleName,
        String description,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
