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
        List<Institute> institutes = (actor == null || isAdministrative(actor))
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
        if (actor != null && !isAdministrative(actor)
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
        validateReferenceFrom(request.referenceFrom());
        Employee creator = requireEmployee(actor);
        Institute institute = new Institute();
        applyRequest(institute, request);
        institute.setCreatedBy(creator);
        institute.setUpdatedBy(creator);
        if (request.assignedEmployeeId() != null) {
            institute.setAssignedEmployee(requireEmployeeById(request.assignedEmployeeId()));
        }

        Institute saved = instituteRepository.save(institute);
        InstituteResponse response = DtoMapper.institute(saved);
        if (creator != null) {
            auditLogService.record(creator, "institutes", saved.getInstituteId(), "CREATE",
                    null, objectMapper.valueToTree(response), null);
        }
        return response;
    }

    @Override
    @Transactional
    public InstituteResponse update(Long instituteId, InstituteRequest request, EmployeePrincipal actor) {
        Institute institute = requireVisibleEntity(instituteId, actor);
        validateReferenceFrom(request.referenceFrom());
        Employee updater = requireEmployee(actor);
        InstituteResponse oldState = DtoMapper.institute(institute);
        applyRequest(institute, request);
        institute.setUpdatedBy(updater);
        if (request.assignedEmployeeId() != null) {
            institute.setAssignedEmployee(requireEmployeeById(request.assignedEmployeeId()));
        }

        Institute saved = instituteRepository.save(institute);
        InstituteResponse response = DtoMapper.institute(saved);
        if (updater != null) {
            auditLogService.record(updater, "institutes", saved.getInstituteId(), "UPDATE",
                    objectMapper.valueToTree(oldState), objectMapper.valueToTree(response), null);
        }
        return response;
    }

    @Override
    @Transactional
    public void delete(Long instituteId, EmployeePrincipal actor) {
        if (instituteId == null) return;
        Institute institute = instituteRepository.findById(instituteId).orElse(null);
        if (institute == null) return;
        Employee deleter = requireEmployee(actor);
        InstituteResponse oldState = DtoMapper.institute(institute);
        instituteRepository.delete(institute);
        if (deleter != null) {
            auditLogService.record(deleter, "institutes", instituteId, "DELETE",
                    objectMapper.valueToTree(oldState), null, null);
        }
    }

    private void applyRequest(Institute institute, InstituteRequest request) {
        institute.setInstituteName(request.instituteName().trim());
        institute.setStreetAddress(request.streetAddress());
        institute.setVillage(request.village());
        institute.setTaluka(request.taluka());
        institute.setDistrict(request.district());
        institute.setState(request.state());
        String pin = request.pinCode() != null ? request.pinCode().trim() : null;
        institute.setPinCode((pin != null && pin.matches("^[0-9]{6}$")) ? pin : null);
        institute.setBranchCount((request.branchCount() != null && request.branchCount() >= 1) ? request.branchCount() : 1);
        institute.setReferenceBy(request.referenceBy());
        String refFrom = request.referenceFrom();
        institute.setReferenceFrom((refFrom != null && REFERENCE_SOURCES.contains(refFrom)) ? refFrom : "Employee");
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

    private Employee requireEmployeeById(Long employeeId) {
        if (employeeId == null) return null;
        return employeeRepository.findById(employeeId).orElse(null);
    }

    private Employee requireEmployee(EmployeePrincipal actor) {
        if (actor != null && actor.getEmployeeId() != null) {
            return employeeRepository.findById(actor.getEmployeeId())
                    .orElseGet(this::getDefaultAdminEmployee);
        }
        return getDefaultAdminEmployee();
    }

    private Employee getDefaultAdminEmployee() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getRole() != null && "Administrative".equalsIgnoreCase(e.getRole().getRoleName()))
                .findFirst()
                .or(() -> employeeRepository.findAll().stream().findFirst())
                .orElse(null);
    }

    private boolean isAdministrative(EmployeePrincipal actor) {
        if (actor == null || actor.getRoleName() == null) {
            return true;
        }
        return actor.getRoleName().equalsIgnoreCase("Administrative");
    }
}
