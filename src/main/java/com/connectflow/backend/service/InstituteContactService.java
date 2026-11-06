package com.connectflow.backend.service;

import com.connectflow.backend.dto.InstituteContactRequest;
import com.connectflow.backend.dto.InstituteContactResponse;
import com.connectflow.backend.security.EmployeePrincipal;

import java.util.List;

public interface InstituteContactService {
    List<InstituteContactResponse> findForInstitute(Long instituteId, EmployeePrincipal actor);

    List<InstituteContactResponse> findAll();

    InstituteContactResponse create(InstituteContactRequest request, Long actorId);

    InstituteContactResponse update(Long contactId, InstituteContactRequest request, Long actorId);
}
