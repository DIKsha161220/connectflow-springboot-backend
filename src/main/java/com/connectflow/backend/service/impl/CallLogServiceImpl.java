package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.CallLogRequest;
import com.connectflow.backend.dto.CallLogResponse;
import com.connectflow.backend.entity.CallLog;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.entity.InstituteContact;
import com.connectflow.backend.exception.BadRequestException;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.CallLogRepository;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.InstituteContactRepository;
import com.connectflow.backend.security.EmployeePrincipal;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.CallLogService;
import com.connectflow.backend.service.InstituteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;

@Service
public class CallLogServiceImpl implements CallLogService {

    private final CallLogRepository callLogRepository;
    private final EmployeeRepository employeeRepository;
    private final InstituteContactRepository contactRepository;
    private final InstituteService instituteService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public CallLogServiceImpl(
            CallLogRepository callLogRepository,
            EmployeeRepository employeeRepository,
            InstituteContactRepository contactRepository,
            InstituteService instituteService,
            AuditLogService auditLogService,
            ObjectMapper objectMapper) {
        this.callLogRepository = callLogRepository;
        this.employeeRepository = employeeRepository;
        this.contactRepository = contactRepository;
        this.instituteService = instituteService;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public CallLogResponse create(
            CallLogRequest request,
            EmployeePrincipal actorPrincipal,
            InetAddress ipAddress) {
        String role = actorPrincipal != null ? actorPrincipal.getRoleName() : "Administrative";
        if (!role.equalsIgnoreCase("Administrative")
                && !role.equalsIgnoreCase("Calling Team")
                && !role.equalsIgnoreCase("On-field Employee")) {
            throw new AccessDeniedException("This role cannot record calls");
        }
        Institute institute = instituteService.requireVisibleEntity(
                request.instituteId(), actorPrincipal);
        Long actorId = actorPrincipal != null ? actorPrincipal.getEmployeeId() : null;
        Employee actor = requireEmployee(actorId);

        CallLog call = new CallLog();
        call.setInstitute(institute);
        call.setEmployee(actor);
        call.setCallType(request.callType().getValue());
        call.setCallDatetime(request.callDatetime());
        call.setContactPerson(request.contactPerson());
        call.setContactPhone(request.contactPhone());
        call.setCallOutcome(request.callOutcome().getValue());
        call.setNextFollowUpAt(request.nextFollowUpAt());
        call.setNotes(request.notes());
        if (request.contactId() != null) {
            InstituteContact contact = contactRepository.findById(request.contactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Institute contact not found"));
            if (!contact.getInstitute().getInstituteId().equals(institute.getInstituteId())) {
                throw new BadRequestException("The selected contact does not belong to this institute");
            }
            call.setContact(contact);
        }

        CallLog saved = callLogRepository.save(call);
        CallLogResponse response = DtoMapper.call(saved);
        if (actor != null) {
            auditLogService.record(actor, "call_logs", saved.getCallId(), "CREATE",
                    null, objectMapper.valueToTree(response), ipAddress);
        }
        return response;
    }

    @Override
    @Transactional
    public CallLogResponse update(
            Long callId,
            CallLogRequest request,
            EmployeePrincipal actorPrincipal,
            InetAddress ipAddress) {
        String role = actorPrincipal != null ? actorPrincipal.getRoleName() : "Administrative";
        if (!role.equalsIgnoreCase("Administrative")
                && !role.equalsIgnoreCase("Calling Team")
                && !role.equalsIgnoreCase("On-field Employee")) {
            throw new AccessDeniedException("This role cannot modify calls");
        }
        CallLog call = callLogRepository.findById(callId)
                .orElseThrow(() -> new ResourceNotFoundException("Call log not found"));
        instituteService.requireVisibleEntity(call.getInstitute().getInstituteId(), actorPrincipal);
        Institute targetInstitute = instituteService.requireVisibleEntity(
                request.instituteId(), actorPrincipal);
        Long actorId = actorPrincipal != null ? actorPrincipal.getEmployeeId() : null;
        Employee actor = requireEmployee(actorId);

        CallLogResponse before = DtoMapper.call(call);

        call.setInstitute(targetInstitute);
        call.setCallType(request.callType().getValue());
        call.setCallDatetime(request.callDatetime());
        call.setContactPerson(request.contactPerson());
        call.setContactPhone(request.contactPhone());
        call.setCallOutcome(request.callOutcome().getValue());
        call.setNextFollowUpAt(request.nextFollowUpAt());
        call.setNotes(request.notes());

        if (request.contactId() != null) {
            InstituteContact contact = contactRepository.findById(request.contactId())
                    .orElseThrow(() -> new ResourceNotFoundException("Institute contact not found"));
            if (!contact.getInstitute().getInstituteId().equals(targetInstitute.getInstituteId())) {
                throw new BadRequestException("The selected contact does not belong to this institute");
            }
            call.setContact(contact);
        } else {
            call.setContact(null);
        }

        CallLog saved = callLogRepository.save(call);
        CallLogResponse response = DtoMapper.call(saved);
        if (actor != null) {
            auditLogService.record(actor, "call_logs", saved.getCallId(), "UPDATE",
                    objectMapper.valueToTree(before), objectMapper.valueToTree(response), ipAddress);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CallLogResponse findById(Long callId, EmployeePrincipal actor) {
        CallLog call = callLogRepository.findById(callId)
                .orElseThrow(() -> new ResourceNotFoundException("Call log not found"));
        instituteService.requireVisibleEntity(call.getInstitute().getInstituteId(), actor);
        return DtoMapper.call(call);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallLogResponse> findAll(EmployeePrincipal actor) {
        List<CallLog> calls;
        if (actor == null || actor.getRoleName() == null || actor.getRoleName().equalsIgnoreCase("Administrative")) {
            calls = callLogRepository.findAllByOrderByCallDatetimeDesc();
        } else {
            calls = callLogRepository.findAllByEmployee_EmployeeIdOrderByCallDatetimeDesc(actor.getEmployeeId());
        }
        return calls.stream().map(DtoMapper::call).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallLogResponse> findForInstitute(Long instituteId, EmployeePrincipal actor) {
        String role = actor != null ? actor.getRoleName() : "Administrative";
        if (!role.equalsIgnoreCase("Administrative")
                && !role.equalsIgnoreCase("Calling Team")) {
            throw new AccessDeniedException("This role cannot read call history");
        }
        instituteService.requireVisibleEntity(instituteId, actor);
        return callLogRepository.findAllByInstitute_InstituteIdOrderByCallDatetimeDesc(instituteId)
                .stream().map(DtoMapper::call).toList();
    }

    @Override
    @Transactional
    public void delete(Long callId, EmployeePrincipal actor) {
        CallLog call = callLogRepository.findById(callId)
                .orElseThrow(() -> new ResourceNotFoundException("Call log not found"));
        callLogRepository.delete(call);
    }

    private Employee requireEmployee(Long employeeId) {
        if (employeeId != null) {
            return employeeRepository.findById(employeeId)
                    .orElseGet(() -> employeeRepository.findAll().stream().findFirst().orElse(null));
        }
        return employeeRepository.findAll().stream().findFirst().orElse(null);
    }
}
