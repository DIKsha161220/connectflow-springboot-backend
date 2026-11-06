package com.connectflow.backend.service;

import com.connectflow.backend.dto.EmployeeRequest;
import com.connectflow.backend.dto.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> findAll();

    EmployeeResponse findById(Long employeeId);

    EmployeeResponse create(EmployeeRequest request, Long actorId);

    EmployeeResponse update(Long employeeId, EmployeeRequest request, Long actorId);
}
