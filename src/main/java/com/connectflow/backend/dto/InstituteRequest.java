package com.connectflow.backend.dto;

import com.connectflow.backend.enums.LeadStatus;
import com.connectflow.backend.enums.Priority;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record InstituteRequest(
        @JsonAlias({"institute_name", "name"})
        @Size(max = 255) String instituteName,
        @JsonAlias("street_address")
        @Size(max = 255) String streetAddress,
        @Size(max = 100) String village,
        @Size(max = 100) String taluka,
        @Size(max = 100) String district,
        @Size(max = 100) String state,
        @JsonAlias({"pin_code", "pincode", "zip"})
        @Pattern(regexp = "^(|[0-9]{6})$", message = "PIN code must be exactly 6 digits or empty")
        String pinCode,
        @JsonAlias({"branch_count", "branches"})
        @Min(1) Integer branchCount,
        @JsonAlias("reference_by")
        @Size(max = 150) String referenceBy,
        @JsonAlias("reference_from")
        @Size(max = 50) String referenceFrom,
        @JsonAlias({"assigned_employee_id", "employee_id"})
        Long assignedEmployeeId,
        @JsonAlias("lead_status")
        LeadStatus leadStatus,
        Priority priority,
        @JsonAlias({"next_follow_up_at", "nextFollowUp"})
        OffsetDateTime nextFollowUpAt,
        String notes,
        @JsonAlias({"is_active", "active"})
        Boolean active) {
}
