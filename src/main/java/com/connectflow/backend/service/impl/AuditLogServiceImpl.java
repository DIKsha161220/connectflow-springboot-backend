package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.AuditLogResponse;
import com.connectflow.backend.entity.AuditLog;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.AuditLogRepository;
import com.connectflow.backend.service.AuditLogService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> findAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(DtoMapper::audit).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse findById(Long auditId) {
        return auditLogRepository.findById(auditId)
                .map(DtoMapper::audit)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found"));
    }

    @Override
    @Transactional
    public void record(
            Employee actor,
            String entityType,
            Long entityId,
            String action,
            JsonNode oldValues,
            JsonNode newValues,
            InetAddress ipAddress) {
        AuditLog log = new AuditLog();
        log.setEmployee(actor);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setOldValues(oldValues);
        log.setNewValues(newValues);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }
}
