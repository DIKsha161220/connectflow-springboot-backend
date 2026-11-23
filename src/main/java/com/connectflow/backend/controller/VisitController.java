package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.VisitRequest;
import com.connectflow.backend.dto.VisitResponse;
import com.connectflow.backend.dto.VisitStatusRequest;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.security.IpAddressResolver;
import com.connectflow.backend.service.VisitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(
            value = {"/{id}/status", "/status"},
            method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<ApiResponse<VisitResponse>> updateStatus(
            @PathVariable(required = false) Long id,
            @Valid @RequestBody VisitStatusRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        Long targetId = id;
        if (targetId == null) {
            List<VisitResponse> all = visitService.findAll(actor);
            if (!all.isEmpty()) targetId = all.get(0).visitId();
        }
        if (targetId == null) {
            throw new ResourceNotFoundException("Visit not found");
        }
        return ResponseEntity.ok(ApiResponse.success(
                "Visit status updated",
                visitService.updateStatus(
                        targetId, request.status(), actor, ipAddressResolver.from(servletRequest))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        visitService.delete(id, actor);
        return ResponseEntity.ok(ApiResponse.success("Visit deleted", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteWithoutId(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        List<VisitResponse> all = visitService.findAll(actor);
        if (!all.isEmpty()) {
            visitService.delete(all.get(0).visitId(), actor);
        }
        return ResponseEntity.ok(ApiResponse.success("Visit deleted", null));
    }
}
