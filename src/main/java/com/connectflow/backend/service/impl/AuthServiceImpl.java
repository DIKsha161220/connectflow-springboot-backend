package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.LoginRequest;
import com.connectflow.backend.dto.LoginResponse;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.security.JwtService;
import com.connectflow.backend.service.AuditLogService;
import com.connectflow.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public AuthServiceImpl(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuditLogService auditLogService,
            ObjectMapper objectMapper,
            Clock clock) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, InetAddress ipAddress) {
        Employee employee = employeeRepository.findByEmailIgnoreCase(
                        request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (employee.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), employee.getPasswordHash())
                || !Boolean.TRUE.equals(employee.getActive())
                || !Boolean.TRUE.equals(employee.getRole().getActive())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        OffsetDateTime loginTime = OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
        employee.setLastLoginAt(loginTime);
        Employee saved = employeeRepository.save(employee);
        auditLogService.record(saved, "employees", saved.getEmployeeId(), "LOGIN",
                null, objectMapper.valueToTree(Map.of("email", saved.getEmail())), ipAddress);

        return new LoginResponse(
                saved.getEmployeeId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getRole().getRoleName(),
                jwtService.generateToken(saved.getEmail()),
                "Bearer",
                "Login successful");
    }
}
