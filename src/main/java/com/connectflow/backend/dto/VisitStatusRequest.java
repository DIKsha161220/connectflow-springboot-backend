package com.connectflow.backend.dto;

import com.connectflow.backend.enums.VisitStatus;
import jakarta.validation.constraints.NotNull;

public record VisitStatusRequest(@NotNull VisitStatus status) {
}
