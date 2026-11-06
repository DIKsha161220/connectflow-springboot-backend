package com.connectflow.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InstituteContactRequest(
        @NotNull Long instituteId,
        @NotBlank @Size(max = 255) String contactName,
        @Size(max = 255) String designation,
        @Size(max = 255) String phone,
        @Email @Size(max = 255) String email,
        Boolean primary,
        String notes,
        Boolean active) {
}
