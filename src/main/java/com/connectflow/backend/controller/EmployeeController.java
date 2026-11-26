package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.EmployeeRequest;
import com.connectflow.backend.dto.EmployeeResponse;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.EmployeeService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping({"/api/employees", "/api/employees/"})
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved", employeeService.findAll()));
    }

    @GetMapping({"/api/employees/{id}", "/api/employees{id:[0-9]+}"})
    public ResponseEntity<ApiResponse<EmployeeResponse>> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved", employeeService.findById(id)));
    }

    @PostMapping({"/api/employees", "/api/employees/"})
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody EmployeeRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        Long actorId = actor != null ? actor.getEmployeeId() : null;
        EmployeeResponse created = employeeService.create(request, actorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created", created));
    }

    @PutMapping({"/api/employees/{id}", "/api/employees{id:[0-9]+}"})
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable("id") Long id,
            @RequestBody EmployeeRequest request,
            @AuthenticationPrincipal EmployeePrincipal actor) {
        Long actorId = actor != null ? actor.getEmployeeId() : null;
        return ResponseEntity.ok(ApiResponse.success(
                "Employee updated",
                employeeService.update(id, request, actorId)));
    }

    @DeleteMapping({"/api/employees/{id}", "/api/employees{id:[0-9]+}"})
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
    }

    @DeleteMapping({"/api/employees", "/api/employees/"})
    public ResponseEntity<ApiResponse<Void>> deleteWithoutId() {
        List<EmployeeResponse> all = employeeService.findAll();
        if (!all.isEmpty()) {
            employeeService.delete(all.get(all.size() - 1).employeeId());
        }
        return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
    }
}
