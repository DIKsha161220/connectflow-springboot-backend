package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.VisitRequest;
import com.connectflow.backend.dto.VisitResponse;
import com.connectflow.backend.dto.VisitStatusRequest;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.security.IpAddressResolver;
import com.connectflow.backend.service.VisitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final IpAddressResolver ipAddressResolver;

    public VisitController(VisitService visitService, IpAddressResolver ipAddressResolver) {
        this.visitService = visitService;
        this.ipAddressResolver = ipAddressResolver;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VisitResponse>> create(
            @Valid @RequestBody VisitRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        VisitResponse response = visitService.create(
                request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Visit created", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VisitRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        VisitResponse response = visitService.update(
                id, request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Visit updated", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VisitResponse>> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Visit retrieved", visitService.findById(id, actor)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VisitResponse>>> findAll(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Visits retrieved", visitService.findAll(actor)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VisitResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody VisitStatusRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                "Visit status updated",
                visitService.updateStatus(
                        id, request.status(), actor, ipAddressResolver.from(servletRequest))));
    }
}
