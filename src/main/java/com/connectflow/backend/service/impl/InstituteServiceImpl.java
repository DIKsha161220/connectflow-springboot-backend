package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.InstituteRequest;
import com.connectflow.backend.dto.InstituteResponse;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.exception.BadRequestException;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.InstituteRepository;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.InstituteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class InstituteServiceImpl implements InstituteService {

    private static final Set<String> REFERENCE_SOURCES =
            Set.of("Employee", "Existing Institute", "Website", "WhatsApp", "Other");

    private final InstituteRepository instituteRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public InstituteServiceImpl(
            InstituteRepository instituteRepository,
            EmployeeRepository employeeRepository,
            AuditLogService auditLogService,
            ObjectMapper objectMapper) {
        this.instituteRepository = instituteRepository;
        this.employeeRepository = employeeRepository;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstituteResponse> findVisibleTo(EmployeePrincipal actor) {
        List<Institute> institutes = isAdministrative(actor)
                ? instituteRepository.findAll()
                : instituteRepository.findAllByAssignedEmployee_EmployeeIdOrderByInstituteName(
                        actor.getEmployeeId());
        return institutes.stream().map(DtoMapper::institute).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InstituteResponse findVisibleById(Long instituteId, EmployeePrincipal actor) {
        return DtoMapper.institute(requireVisibleEntity(instituteId, actor));
    }

    @Override
    @Transactional(readOnly = true)
    public Institute requireVisibleEntity(Long instituteId, EmployeePrincipal actor) {
        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found"));
        if (!isAdministrative(actor)
                && (institute.getAssignedEmployee() == null
                || !institute.getAssignedEmployee().getEmployeeId().equals(actor.getEmployeeId()))) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "The institute is not assigned to this employee");
        }
        return institute;
    }

    @Override
    @Transactional
    public InstituteResponse create(InstituteRequest request, EmployeePrincipal actor) {
        if (!isAdministrative(actor)) {
            throw new AccessDeniedException("Only Administrative users can create institutes");
        }
        validateReferenceFrom(request.referenceFrom());
        Employee creator = requireEmployee(actor.getEmployeeId());
        Institute institute = new Institute();
        applyRequest(institute, request);
        institute.setCreatedBy(creator);
        institute.setUpdatedBy(creator);
        if (request.assignedEmployeeId() != null) {
            institute.setAssignedEmployee(requireEmployee(request.assignedEmployeeId()));
        }

        Institute saved = instituteRepository.save(institute);
        InstituteResponse response = DtoMapper.institute(saved);
        auditLogService.record(creator, "institutes", saved.getInstituteId(), "CREATE",
                null, objectMapper.valueToTree(response), null);
        return response;
    }

    private void applyRequest(Institute institute, InstituteRequest request) {
        institute.setInstituteName(request.instituteName().trim());
        institute.setStreetAddress(request.streetAddress());
        institute.setVillage(request.village());
        institute.setTaluka(request.taluka());
        institute.setDistrict(request.district());
        institute.setState(request.state());
        institute.setPinCode(request.pinCode());
        institute.setBranchCount(request.branchCount());
        institute.setReferenceBy(request.referenceBy());
        institute.setReferenceFrom(request.referenceFrom());
        institute.setLeadStatus(request.leadStatus() == null ? "New" : request.leadStatus().getValue());
        institute.setPriority(request.priority() == null ? "Medium" : request.priority().getValue());
        institute.setNextFollowUpAt(request.nextFollowUpAt());
        institute.setNotes(request.notes());
        institute.setActive(request.active() == null || request.active());
    }

    private void validateReferenceFrom(String source) {
        if (source != null && !REFERENCE_SOURCES.contains(source)) {
            throw new BadRequestException("referenceFrom is not an allowed guide value");
        }
    }

    private Employee requireEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private boolean isAdministrative(EmployeePrincipal actor) {
        return actor.getRoleName().equalsIgnoreCase("Administrative");
    }
}
