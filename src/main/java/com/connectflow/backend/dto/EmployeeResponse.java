package com.connectflow.backend.dto;

import java.time.OffsetDateTime;

public record EmployeeResponse(
        Long employeeId,
        String fullName,
        String email,
        String phone,
        Long roleId,
        String roleName,
        Boolean active,
        OffsetDateTime lastLoginAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
