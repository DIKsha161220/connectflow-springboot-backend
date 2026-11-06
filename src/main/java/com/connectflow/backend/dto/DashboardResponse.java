package com.connectflow.backend.dto;

public record DashboardResponse(
        long totalEmployees,
        long totalInstitutes,
        long totalContacts,
        long totalCalls,
        long totalVisits,
        long totalFollowUps,
        long pendingFollowUps,
        long completedFollowUps) {
}
