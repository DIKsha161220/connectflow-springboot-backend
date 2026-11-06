package com.connectflow.backend.dto;

import com.connectflow.backend.enums.LeadStatus;
import com.connectflow.backend.enums.Priority;

import java.time.OffsetDateTime;

public record InstituteResponse(
        Long instituteId,
        String instituteName,
        String streetAddress,
        String village,
        String taluka,
        String district,
        String state,
        String pinCode,
        Integer branchCount,
        String referenceBy,
        String referenceFrom,
        Long assignedEmployeeId,
        String assignedEmployeeName,
        LeadStatus leadStatus,
        Priority priority,
        OffsetDateTime nextFollowUpAt,
        String notes,
        Boolean active,
        Long createdBy,
        Long updatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
