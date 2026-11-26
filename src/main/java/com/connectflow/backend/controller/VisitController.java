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
public class VisitController {

    private final VisitService visitService;
    private final IpAddressResolver ipAddressResolver;

    public VisitController(VisitService visitService, IpAddressResolver ipAddressResolver) {
        this.visitService = visitService;
        this.ipAddressResolver = ipAddressResolver;
    }

    @PostMapping({"/api/visits", "/api/visits/"})
    public ResponseEntity<ApiResponse<VisitResponse>> create(
            @Valid @RequestBody VisitRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        VisitResponse response = visitService.create(
                request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Visit created", response));
    }

    @PutMapping({"/api/visits/{id}", "/api/visits{id:[0-9]+}"})
    public ResponseEntity<ApiResponse<VisitResponse>> update(
            @PathVariable("id") Long id,
            @RequestBody VisitRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        VisitResponse response = visitService.update(
                id, request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Visit updated", response));
    }

    @GetMapping({"/api/visits/{id}", "/api/visits{id:[0-9]+}"})
    public ResponseEntity<ApiResponse<VisitResponse>> findById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Visit retrieved", visitService.findById(id, actor)));
    }

    @GetMapping({"/api/visits", "/api/visits/"})
    public ResponseEntity<ApiResponse<List<VisitResponse>>> findAll(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Visits retrieved", visitService.findAll(actor)));
    }

    @RequestMapping(
            value = {"/api/visits/{id}/status", "/api/visits{id:[0-9]+}/status"},
            method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<ApiResponse<VisitResponse>> updateStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody VisitStatusRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(ApiResponse.success(
                "Visit status updated",
                visitService.updateStatus(
                        id, request.status(), actor, ipAddressResolver.from(servletRequest))));
    }

    @RequestMapping(
            value = {"/api/visits/status"},
            method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<ApiResponse<VisitResponse>> updateStatusDefault(
            @Valid @RequestBody VisitStatusRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        List<VisitResponse> all = visitService.findAll(actor);
        Long targetId = all.isEmpty() ? null : all.get(0).visitId();
        if (targetId == null) {
            throw new ResourceNotFoundException("Visit not found");
        }
        return ResponseEntity.ok(ApiResponse.success(
                "Visit status updated",
                visitService.updateStatus(
                        targetId, request.status(), actor, ipAddressResolver.from(servletRequest))));
    }

    @DeleteMapping({"/api/visits/{id}", "/api/visits{id:[0-9]+}"})
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        visitService.delete(id, actor);
        return ResponseEntity.ok(ApiResponse.success("Visit deleted"));
    }

    @DeleteMapping({"/api/visits", "/api/visits/"})
    public ResponseEntity<ApiResponse<Void>> deleteWithoutId(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        List<VisitResponse> all = visitService.findAll(actor);
        if (!all.isEmpty()) {
            visitService.delete(all.get(0).visitId(), actor);
        }
        return ResponseEntity.ok(ApiResponse.success("Visit deleted"));
    }
}
