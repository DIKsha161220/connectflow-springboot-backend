package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.InstituteContactRequest;
import com.connectflow.backend.dto.InstituteContactResponse;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.entity.InstituteContact;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.InstituteContactRepository;
import com.connectflow.backend.repository.InstituteRepository;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.InstituteContactService;
import com.connectflow.backend.service.InstituteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstituteContactServiceImpl implements InstituteContactService {

    private final InstituteContactRepository contactRepository;
    private final InstituteRepository instituteRepository;
    private final EmployeeRepository employeeRepository;
    private final InstituteService instituteService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public InstituteContactServiceImpl(
            InstituteContactRepository contactRepository,
            InstituteRepository instituteRepository,
            EmployeeRepository employeeRepository,
            InstituteService instituteService,
            AuditLogService auditLogService,
            ObjectMapper objectMapper) {
        this.contactRepository = contactRepository;
        this.instituteRepository = instituteRepository;
        this.employeeRepository = employeeRepository;
        this.instituteService = instituteService;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstituteContactResponse> findForInstitute(
            Long instituteId,
            EmployeePrincipal actor) {
        instituteService.requireVisibleEntity(instituteId, actor);
        return contactRepository.findAllByInstitute_InstituteIdOrderByContactName(instituteId)
                .stream().map(DtoMapper::contact).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstituteContactResponse> findAll() {
        return contactRepository.findAll().stream().map(DtoMapper::contact).toList();
    }

    @Override
    @Transactional
    public InstituteContactResponse create(InstituteContactRequest request, Long actorId) {
        Employee actor = requireAdministrative(actorId);
        InstituteContact contact = new InstituteContact();
        apply(contact, request);
        InstituteContact saved = contactRepository.save(contact);
        InstituteContactResponse response = DtoMapper.contact(saved);
        auditLogService.record(actor, "institute_contacts", saved.getContactId(), "CREATE",
                null, objectMapper.valueToTree(response), null);
        return response;
    }

    @Override
    @Transactional
    public InstituteContactResponse update(
            Long contactId,
            InstituteContactRequest request,
            Long actorId) {
        Employee actor = requireAdministrative(actorId);
        InstituteContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Institute contact not found"));
        InstituteContactResponse before = DtoMapper.contact(contact);
        apply(contact, request);
        InstituteContactResponse response = DtoMapper.contact(contactRepository.save(contact));
        auditLogService.record(actor, "institute_contacts", contactId, "UPDATE",
                objectMapper.valueToTree(before), objectMapper.valueToTree(response), null);
        return response;
    }

    private void apply(InstituteContact contact, InstituteContactRequest request) {
        Institute institute = instituteRepository.findById(request.instituteId())
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found"));
        contact.setInstitute(institute);
        contact.setContactName(request.contactName().trim());
        contact.setDesignation(request.designation());
        contact.setPhone(request.phone());
        contact.setEmail(request.email());
        contact.setPrimary(request.primary() != null && request.primary());
        contact.setNotes(request.notes());
        contact.setActive(request.active() == null || request.active());
    }

    private Employee requireAdministrative(Long actorId) {
        Employee actor = employeeRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (!actor.getRole().getRoleName().equalsIgnoreCase("Administrative")) {
            throw new AccessDeniedException("Only Administrative users can manage contacts");
        }
        return actor;
    }
}
