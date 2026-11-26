package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.InstituteContactRequest;
import com.connectflow.backend.dto.InstituteContactResponse;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.InstituteContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InstituteContactController {

    private final InstituteContactService contactService;

    public InstituteContactController(InstituteContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/api/institutes/{instituteId}/contacts")
    public ResponseEntity<ApiResponse<List<InstituteContactResponse>>> findForInstitute(
            @PathVariable("instituteId") Long instituteId,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Institute contacts retrieved",
                contactService.findForInstitute(instituteId, actor)));
    }

    @GetMapping("/api/institute-contacts")
    public ResponseEntity<ApiResponse<List<InstituteContactResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Contacts retrieved", contactService.findAll()));
    }

    @PostMapping("/api/institute-contacts")
    public ResponseEntity<ApiResponse<InstituteContactResponse>> create(
            @Valid @RequestBody InstituteContactRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        Long actorId = actor != null ? actor.getEmployeeId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Contact created",
                contactService.create(request, actorId)));
    }

    @PutMapping("/api/institute-contacts/{id}")
    public ResponseEntity<ApiResponse<InstituteContactResponse>> update(
            @PathVariable("id") Long id,
            @RequestBody InstituteContactRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        Long actorId = actor != null ? actor.getEmployeeId() : null;
        return ResponseEntity.ok(ApiResponse.success(
                "Contact updated",
                contactService.update(id, request, actorId)));
    }
}
