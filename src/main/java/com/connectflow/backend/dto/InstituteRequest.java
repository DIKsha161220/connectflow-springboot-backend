package com.connectflow.backend.dto;

import com.connectflow.backend.enums.LeadStatus;
import com.connectflow.backend.enums.Priority;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record InstituteRequest(
        @NotBlank @Size(max = 255) String instituteName,
        @Size(max = 255) String streetAddress,
        @Size(max = 100) String village,
        @Size(max = 100) String taluka,
        @Size(max = 100) String district,
        @Size(max = 100) String state,
        @Pattern(regexp = "^[0-9]{6}$") String pinCode,
        @Min(1) Integer branchCount,
        @Size(max = 150) String referenceBy,
        @Size(max = 50) String referenceFrom,
        Long assignedEmployeeId,
        LeadStatus leadStatus,
        Priority priority,
        OffsetDateTime nextFollowUpAt,
        String notes,
        Boolean active) {
}
