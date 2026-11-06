package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.FollowUpRequest;
import com.connectflow.backend.dto.FollowUpResponse;
import com.connectflow.backend.entity.CallLog;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.FollowUp;
import com.connectflow.backend.entity.Institute;
import com.connectflow.backend.enums.FollowUpStatus;
import com.connectflow.backend.exception.BadRequestException;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.CallLogRepository;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.FollowUpRepository;
import com.connectflow.backend.repository.InstituteRepository;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.FollowUpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class FollowUpServiceImpl implements FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final InstituteRepository instituteRepository;
    private final EmployeeRepository employeeRepository;
    private final CallLogRepository callLogRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public FollowUpServiceImpl(
            FollowUpRepository followUpRepository,
            InstituteRepository instituteRepository,
            EmployeeRepository employeeRepository,
            CallLogRepository callLogRepository,
            AuditLogService auditLogService,
            ObjectMapper objectMapper,
            Clock clock) {
        this.followUpRepository = followUpRepository;
        this.instituteRepository = instituteRepository;
        this.employeeRepository = employeeRepository;
        this.callLogRepository = callLogRepository;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowUpResponse> findAll() {
        return followUpRepository.findAll().stream().map(DtoMapper::followUp).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FollowUpResponse findById(Long followUpId) {
        return followUpRepository.findById(followUpId)
                .map(DtoMapper::followUp)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found"));
    }

    @Override
    @Transactional
    public FollowUpResponse create(FollowUpRequest request, Long actorId, InetAddress ipAddress) {
        Employee actor = requireEmployee(actorId);
        Institute institute = instituteRepository.findById(request.instituteId())
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found"));
        FollowUp followUp = new FollowUp();
        followUp.setInstitute(institute);
        followUp.setEmployee(actor);
        followUp.setCreatedBy(actor);
        followUp.setUpdatedBy(actor);
        followUp.setScheduledAt(request.scheduledAt());
        followUp.setFollowUpStatus(FollowUpStatus.PENDING.getValue());
        followUp.setNotes(request.notes());
        if (request.relatedCallId() != null) {
            CallLog call = callLogRepository.findById(request.relatedCallId())
                    .orElseThrow(() -> new ResourceNotFoundException("Call log not found"));
            if (!call.getInstitute().getInstituteId().equals(institute.getInstituteId())) {
                throw new BadRequestException("The selected call does not belong to this institute");
            }
            followUp.setRelatedCall(call);
        }

        FollowUp saved = followUpRepository.save(followUp);
        FollowUpResponse response = DtoMapper.followUp(saved);
        auditLogService.record(actor, "follow_ups", saved.getFollowUpId(), "CREATE",
                null, objectMapper.valueToTree(response), ipAddress);
        return response;
    }

    @Override
    @Transactional
    public FollowUpResponse complete(Long followUpId, Long actorId, InetAddress ipAddress) {
        Employee actor = requireEmployee(actorId);
        FollowUp followUp = followUpRepository.findById(followUpId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow-up not found"));
        if (FollowUpStatus.COMPLETED.getValue().equals(followUp.getFollowUpStatus())) {
            throw new BadRequestException("Follow-up is already completed");
        }
        FollowUpResponse before = DtoMapper.followUp(followUp);
        followUp.setFollowUpStatus(FollowUpStatus.COMPLETED.getValue());
        followUp.setCompletedAt(OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
        followUp.setCompletedBy(actor);
        followUp.setUpdatedBy(actor);

        FollowUpResponse response = DtoMapper.followUp(followUpRepository.save(followUp));
        auditLogService.record(actor, "follow_ups", followUpId, "UPDATE",
                objectMapper.valueToTree(before), objectMapper.valueToTree(response), ipAddress);
        return response;
    }

    private Employee requireEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }
}
