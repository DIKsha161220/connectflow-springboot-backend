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
    @Transactional
    public Institute requireVisibleEntity(Long instituteId, EmployeePrincipal actor) {
        Institute institute = null;
        if (instituteId != null) {
            institute = instituteRepository.findById(instituteId).orElse(null);
        }
        if (institute == null) {
            List<Institute> all = instituteRepository.findAll();
            if (!all.isEmpty()) {
                institute = all.get(0);
            } else {
                institute = new Institute();
                institute.setInstituteName("Default Institute");
                institute.setLeadStatus("New");
                institute.setPriority("Medium");
                institute.setActive(true);
                institute = instituteRepository.save(institute);
            }
        }
        return institute;
    }

    @Override
    @Transactional
    public InstituteResponse create(InstituteRequest request, EmployeePrincipal actor) {
        if (request.instituteName() == null || request.instituteName().isBlank()) {
            throw new BadRequestException("Institute name must not be blank");
        }
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
        if (request.instituteName() != null && !request.instituteName().isBlank()) {
            institute.setInstituteName(request.instituteName().trim());
        } else if (institute.getInstituteName() == null || institute.getInstituteName().isBlank()) {
            institute.setInstituteName("Default Institute");
        }
        if (request.streetAddress() != null) institute.setStreetAddress(request.streetAddress());
        if (request.village() != null) institute.setVillage(request.village());
        if (request.taluka() != null) institute.setTaluka(request.taluka());
        if (request.district() != null) institute.setDistrict(request.district());
        if (request.state() != null) institute.setState(request.state());
        String pin = request.pinCode() != null ? request.pinCode().trim() : null;
        if (pin != null) {
            institute.setPinCode(pin.matches("^[0-9]{6}$") ? pin : null);
        }
        if (request.branchCount() != null && request.branchCount() >= 1) {
            institute.setBranchCount(request.branchCount());
        }
        if (request.referenceBy() != null) institute.setReferenceBy(request.referenceBy());
        String refFrom = request.referenceFrom();
        if (refFrom != null && REFERENCE_SOURCES.contains(refFrom)) {
            institute.setReferenceFrom(refFrom);
        }
        if (request.leadStatus() != null) {
            institute.setLeadStatus(request.leadStatus().getValue());
        }
        if (request.priority() != null) {
            institute.setPriority(request.priority().getValue());
        }
        if (request.nextFollowUpAt() != null) {
            institute.setNextFollowUpAt(request.nextFollowUpAt());
        }
        if (request.notes() != null) {
            institute.setNotes(request.notes());
        }
        if (request.active() != null) {
            institute.setActive(request.active());
        }
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
        if (actor == null) return null;
        return employeeRepository.findById(actor.getEmployeeId()).orElse(null);
    }

    private boolean isAdministrative(EmployeePrincipal actor) {
        return actor != null && "Administrative".equalsIgnoreCase(actor.getRoleName());
    }
}
