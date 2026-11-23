package com.connectflow.backend.controller;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.dto.DashboardResponse;
import com.connectflow.backend.dto.EmployeeResponse;
import com.connectflow.backend.dto.InstituteResponse;
import com.connectflow.backend.service.DashboardService;
import com.connectflow.backend.service.EmployeeService;
import com.connectflow.backend.service.InstituteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final EmployeeService employeeService;
    private final InstituteService instituteService;

    public DashboardController(
            DashboardService dashboardService,
            EmployeeService employeeService,
            InstituteService instituteService) {
        this.dashboardService = dashboardService;
        this.employeeService = employeeService;
        this.instituteService = instituteService;
    }

    @GetMapping({"", "/", "/summary"})
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard retrieved", dashboardService.getDashboard()));
    }

    @GetMapping({"/employee", "/employees"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmployeeData() {
        List<EmployeeResponse> employees = employeeService.findAll();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("employees", employees);
        data.put("employeeCount", employees.size());
        return ResponseEntity.ok(ApiResponse.success("Dashboard employee data retrieved", data));
    }

    @GetMapping({"/institute", "/institutes"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInstituteData() {
        List<InstituteResponse> institutes = instituteService.findVisibleTo(null);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("institutes", institutes);
        data.put("instituteCount", institutes.size());
        return ResponseEntity.ok(ApiResponse.success("Dashboard institute data retrieved", data));
    }

    @GetMapping({"/statistics", "/stats"})
    public ResponseEntity<ApiResponse<DashboardResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved", dashboardService.getDashboard()));
    }
}
