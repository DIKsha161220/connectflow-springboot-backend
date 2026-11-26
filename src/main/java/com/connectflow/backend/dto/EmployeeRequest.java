package com.connectflow.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @JsonAlias({"full_name", "name"})
        @Size(max = 255) String fullName,
        @Email @Size(max = 255) String email,
        @Size(max = 255) String phone,
        @JsonAlias("role_id")
        Long roleId,
        @Size(min = 4, max = 72) String password,
        @JsonAlias({"is_active", "active"})
        Boolean active) {
}
