package com.connectflow.backend.dto;

public record LoginResponse(
        Long employeeId,
        String fullName,
        String email,
        String role,
        String token,
        String tokenType,
        String message) {
}
