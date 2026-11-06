package com.connectflow.backend.service;

import com.connectflow.backend.dto.InstituteRequest;
import com.connectflow.backend.dto.InstituteResponse;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.security.EmployeePrincipal;

import java.util.List;

public interface InstituteService {
    List<InstituteResponse> findVisibleTo(EmployeePrincipal actor);

    InstituteResponse findVisibleById(Long instituteId, EmployeePrincipal actor);

    Institute requireVisibleEntity(Long instituteId, EmployeePrincipal actor);

    InstituteResponse create(InstituteRequest request, EmployeePrincipal actor);
}
