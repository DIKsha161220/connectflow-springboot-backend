package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.CallLogRequest;
import com.connectflow.backend.dto.CallLogResponse;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.security.IpAddressResolver;
import com.connectflow.backend.service.CallLogService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CallLogController {

    private final CallLogService callLogService;
    private final IpAddressResolver ipAddressResolver;

    public CallLogController(CallLogService callLogService, IpAddressResolver ipAddressResolver) {
        this.callLogService = callLogService;
        this.ipAddressResolver = ipAddressResolver;
    }

    @PostMapping({"/calls", "/call-logs", "/call-log"})
    public ResponseEntity<ApiResponse<CallLogResponse>> create(
            @Valid @RequestBody CallLogRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        CallLogResponse response = callLogService.create(
                request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Call recorded", response));
    }

    @PutMapping({"/calls/{id}", "/call-logs/{id}", "/call-log/{id}"})
    public ResponseEntity<ApiResponse<CallLogResponse>> update(
            @PathVariable("id") Long id,
            @RequestBody CallLogRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        CallLogResponse response = callLogService.update(
                id, request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Call updated", response));
    }

    @PutMapping({"/calls", "/call-logs", "/call-log"})
    public ResponseEntity<ApiResponse<CallLogResponse>> updateWithoutId(
            @RequestBody CallLogRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor,
            HttpServletRequest servletRequest) {
        List<CallLogResponse> all = callLogService.findAll(actor);
        Long targetId = !all.isEmpty() ? all.get(0).callId() : null;
        if (targetId == null) {
            CallLogResponse created = callLogService.create(request, actor, ipAddressResolver.from(servletRequest));
            return ResponseEntity.ok(ApiResponse.success("Call updated", created));
        }
        CallLogResponse response = callLogService.update(
                targetId, request, actor, ipAddressResolver.from(servletRequest));
        return ResponseEntity.ok(ApiResponse.success("Call updated", response));
    }

    @GetMapping({"/calls/{id}", "/call-logs/{id}"})
    public ResponseEntity<ApiResponse<CallLogResponse>> findById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Call retrieved", callLogService.findById(id, actor)));
    }

    @GetMapping({"/calls", "/call-logs", "/call-log"})
    public ResponseEntity<ApiResponse<List<CallLogResponse>>> findAll(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Calls retrieved", callLogService.findAll(actor)));
    }

    @GetMapping("/institutes/{instituteId}/calls")
    public ResponseEntity<ApiResponse<List<CallLogResponse>>> findForInstitute(
            @PathVariable("instituteId") Long instituteId,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        return ResponseEntity.ok(ApiResponse.success(
                "Call history retrieved", callLogService.findForInstitute(instituteId, actor)));
    }

    @DeleteMapping({"/calls/{id}", "/call-logs/{id}"})
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        callLogService.delete(id, actor);
        return ResponseEntity.ok(ApiResponse.success("Call log deleted", null));
    }

    @DeleteMapping({"/calls", "/call-logs", "/call-log"})
    public ResponseEntity<ApiResponse<Void>> deleteWithoutId(
            @AuthenticationPrincipal EmployeePrincipal actor) {
        List<CallLogResponse> all = callLogService.findAll(actor);
        if (!all.isEmpty()) {
            callLogService.delete(all.get(0).callId(), actor);
        }
        return ResponseEntity.ok(ApiResponse.success("Call log deleted", null));
    }
}
