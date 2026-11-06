package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.EmployeeRequest;
import com.connectflow.backend.dto.EmployeeResponse;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Role;
import com.connectflow.backend.exception.BadRequestException;
import com.connectflow.backend.exception.ResourceNotFoundException;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.RoleRepository;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService,
            ObjectMapper objectMapper) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream().map(DtoMapper::employee).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long employeeId) {
        return DtoMapper.employee(requireEmployee(employeeId));
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request, Long actorId) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException("A password is required when creating an employee");
        }
        if (employeeRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BadRequestException("An employee with this email already exists");
        }

        Employee actor = requireEmployee(actorId);
        Employee employee = new Employee();
        employee.setFullName(request.fullName().trim());
        employee.setEmail(request.email().trim().toLowerCase());
        employee.setPhone(request.phone());
        employee.setPasswordHash(passwordEncoder.encode(request.password()));
        employee.setRole(requireActiveRole(request.roleId()));
        employee.setActive(request.active() == null || request.active());
        Employee saved = employeeRepository.save(employee);
        EmployeeResponse response = DtoMapper.employee(saved);
        auditLogService.record(actor, "employees", saved.getEmployeeId(), "CREATE",
                null, objectMapper.valueToTree(response), null);
        return response;
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long employeeId, EmployeeRequest request, Long actorId) {
        Employee actor = requireEmployee(actorId);
        Employee employee = requireEmployee(employeeId);
        if (!employee.getEmail().equalsIgnoreCase(request.email())
                && employeeRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BadRequestException("An employee with this email already exists");
        }

        EmployeeResponse before = DtoMapper.employee(employee);
        employee.setFullName(request.fullName().trim());
        employee.setEmail(request.email().trim().toLowerCase());
        employee.setPhone(request.phone());
        employee.setRole(requireActiveRole(request.roleId()));
        if (request.active() != null) {
            employee.setActive(request.active());
        }
        if (request.password() != null && !request.password().isBlank()) {
            employee.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        EmployeeResponse response = DtoMapper.employee(employeeRepository.save(employee));
        auditLogService.record(actor, "employees", employeeId, "UPDATE",
                objectMapper.valueToTree(before), objectMapper.valueToTree(response), null);
        return response;
    }

    private Employee requireEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    private Role requireActiveRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        if (!Boolean.TRUE.equals(role.getActive())) {
            throw new BadRequestException("The selected role is inactive");
        }
        return role;
    }
}
