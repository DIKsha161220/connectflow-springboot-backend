package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.FollowUpRequest;
import com.connectflow.backend.dto.FollowUpResponse;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.security.IpAddressResolver;
import com.connectflow.backend.service.FollowUpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpController {

    private final FollowUpService followUpService;
    private final IpAddressResolver ipAddressResolver;

    public FollowUpController(FollowUpService followUpService, IpAddressResolver ipAddressResolver) {
        this.followUpService = followUpService;
        this.ipAddressResolver = ipAddressResolver;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FollowUpResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Follow-ups retrieved", followUpService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FollowUpResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Follow-up retrieved", followUpService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FollowUpResponse>> create(
            @Valid @RequestBody FollowUpRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Follow-up created",
                followUpService.create(
                        request, actor.getEmployeeId(), ipAddressResolver.from(servletRequest))));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<FollowUpResponse>> complete(
            @PathVariable Long id,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                "Follow-up completed",
                followUpService.complete(
                        id, actor.getEmployeeId(), ipAddressResolver.from(servletRequest))));
    }
}
