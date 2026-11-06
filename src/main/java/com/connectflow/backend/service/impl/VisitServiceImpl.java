package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.VisitRequest;
import com.connectflow.backend.dto.VisitResponse;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.entity.InstituteContact;
import com.connectflow.backend.entity.Visit;
import com.connectflow.backend.enums.VisitStatus;
import com.connectflow.backend.exception.BadRequestException;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.InstituteContactRepository;
import com.connectflow.backend.repository.VisitRepository;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.InstituteService;
import com.connectflow.backend.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;

@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final EmployeeRepository employeeRepository;
    private final InstituteContactRepository contactRepository;
    private final InstituteService instituteService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public VisitServiceImpl(
            VisitRepository visitRepository,
            EmployeeRepository employeeRepository,
            InstituteContactRepository contactRepository,
            InstituteService instituteService,
            AuditLogService auditLogService,
            ObjectMapper objectMapper) {
        this.visitRepository = visitRepository;
        this.employeeRepository = employeeRepository;
        this.contactRepository = contactRepository;
        this.instituteService = instituteService;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public VisitResponse create(VisitRequest request, EmployeePrincipal principal, InetAddress ipAddress) {
        requireVisitAccess(principal);
        Institute institute = instituteService.requireVisibleEntity(request.instituteId(), principal);
        Employee actor = requireEmployee(principal.getEmployeeId());
        Visit visit = new Visit();
        visit.setInstitute(institute);
        visit.setEmployee(actor);
        visit.setVisitDatetime(request.visitDatetime());
        visit.setVisitStatus(request.status() != null ? request.status().getValue() : VisitStatus.SCHEDULED.getValue());
        visit.setContactPerson(request.contactPerson());
        visit.setContactPhone(request.contactPhone());
        visit.setPurpose(request.purpose());
        visit.setNotes(request.notes());
        if (request.contactId() != null) {
            InstituteContact contact = contactRepository.findById(request.contactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Institute contact not found"));
            if (!contact.getInstitute().getInstituteId().equals(institute.getInstituteId())) {
                throw new BadRequestException("The selected contact does not belong to this institute");
            }
            visit.setContact(contact);
        }

        Visit saved = visitRepository.save(visit);
        VisitResponse response = DtoMapper.visit(saved);
        auditLogService.record(actor, "visits", saved.getVisitId(), "CREATE",
                null, objectMapper.valueToTree(response), ipAddress);
        return response;
    }

    @Override
    @Transactional
    public VisitResponse update(
            Long visitId,
            VisitRequest request,
            EmployeePrincipal principal,
            InetAddress ipAddress) {
        requireVisitAccess(principal);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        instituteService.requireVisibleEntity(visit.getInstitute().getInstituteId(), principal);
        Institute targetInstitute = instituteService.requireVisibleEntity(request.instituteId(), principal);
        Employee actor = requireEmployee(principal.getEmployeeId());

        VisitResponse before = DtoMapper.visit(visit);

        visit.setInstitute(targetInstitute);
        visit.setVisitDatetime(request.visitDatetime());
        if (request.status() != null) {
            visit.setVisitStatus(request.status().getValue());
        }
        visit.setContactPerson(request.contactPerson());
        visit.setContactPhone(request.contactPhone());
        visit.setPurpose(request.purpose());
        visit.setNotes(request.notes());

        if (request.contactId() != null) {
            InstituteContact contact = contactRepository.findById(request.contactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Institute contact not found"));
            if (!contact.getInstitute().getInstituteId().equals(targetInstitute.getInstituteId())) {
                throw new BadRequestException("The selected contact does not belong to this institute");
            }
            visit.setContact(contact);
        } else {
            visit.setContact(null);
        }

        Visit saved = visitRepository.save(visit);
        VisitResponse response = DtoMapper.visit(saved);
        auditLogService.record(actor, "visits", saved.getVisitId(), "UPDATE",
                objectMapper.valueToTree(before), objectMapper.valueToTree(response), ipAddress);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public VisitResponse findById(Long visitId, EmployeePrincipal actor) {
        requireVisitAccess(actor);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        instituteService.requireVisibleEntity(visit.getInstitute().getInstituteId(), actor);
        return DtoMapper.visit(visit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> findAll(EmployeePrincipal actor) {
        requireVisitAccess(actor);
        List<Visit> visits;
        if (actor.getRoleName().equalsIgnoreCase("Administrative")) {
            visits = visitRepository.findAllByOrderByVisitDatetimeDesc();
        } else {
            visits = visitRepository.findAllByEmployee_EmployeeIdOrderByVisitDatetimeDesc(actor.getEmployeeId());
        }
        return visits.stream().map(DtoMapper::visit).toList();
    }

    @Override
    @Transactional
    public VisitResponse updateStatus(
            Long visitId,
            VisitStatus status,
            EmployeePrincipal principal,
            InetAddress ipAddress) {
        requireVisitAccess(principal);
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        instituteService.requireVisibleEntity(visit.getInstitute().getInstituteId(), principal);
        Employee actor = requireEmployee(principal.getEmployeeId());
        VisitResponse before = DtoMapper.visit(visit);
        visit.setVisitStatus(status.getValue());
        VisitResponse response = DtoMapper.visit(visitRepository.save(visit));
        auditLogService.record(actor, "visits", visitId, "UPDATE",
                objectMapper.valueToTree(before), objectMapper.valueToTree(response), ipAddress);
        return response;
    }

    private void requireVisitAccess(EmployeePrincipal principal) {
        if (!principal.getRoleName().equalsIgnoreCase("Administrative")
                && !principal.getRoleName().equalsIgnoreCase("On-field Employee")) {
            throw new AccessDeniedException("This role cannot manage visits");
        }
    }

    private Employee requireEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }
}
