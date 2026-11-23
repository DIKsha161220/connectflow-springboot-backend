package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.InstituteRequest;
import com.connectflow.backend.dto.InstituteResponse;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.InstituteService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/institutes")
public class InstituteController {

    private final InstituteService instituteService;

    public InstituteController(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InstituteResponse>>> findAll(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Institutes retrieved", instituteService.findVisibleTo(actor)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InstituteResponse>> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Institute retrieved", instituteService.findVisibleById(id, actor)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstituteResponse>> create(
            @Valid @RequestBody InstituteRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Institute created", instituteService.create(request, actor)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InstituteResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody InstituteRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Institute updated", instituteService.update(id, request, actor)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        instituteService.delete(id, actor);
        return ResponseEntity.ok(ApiResponse.success("Institute deleted", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteWithoutId(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        List<InstituteResponse> all = instituteService.findVisibleTo(actor);
        if (!all.isEmpty()) {
            instituteService.delete(all.get(all.size() - 1).instituteId(), actor);
        }
        return ResponseEntity.ok(ApiResponse.success("Institute deleted", null));
    }
}
