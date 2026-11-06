package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.EmployeeRequest;
import com.connectflow.backend.dto.EmployeeResponse;
import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Role;
import com.connectflow.backend.exception.BadRequestException;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.RoleRepository;
import com.connectflow.backend.service.AuditLogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void rejectsEmployeeWithoutPasswordBeforeDatabaseWrites() {
        EmployeeRequest request = new EmployeeRequest(
                "Test Employee", "test@example.com", null, 2L, null, true);

        assertThrows(BadRequestException.class, () -> employeeService.create(request, 1L));

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void storesOnlyEncodedPasswordWhenCreatingEmployee() {
        Employee actor = new Employee();
        Role role = new Role();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(actor));
        when(employeeRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secure-password")).thenReturn("$2a$hashed-value");
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(objectMapper.valueToTree(any(EmployeeResponse.class))).thenReturn(null);

        EmployeeRequest request = new EmployeeRequest(
                "Test Employee", "test@example.com", null, 2L, "secure-password", true);
        employeeService.create(request, 1L);

        ArgumentCaptor<Employee> saved = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(saved.capture());
        assertNotEquals("secure-password", saved.getValue().getPasswordHash());
        verify(passwordEncoder).encode("secure-password");
        verify(auditLogService).record(
                any(Employee.class),
                eq("employees"),
                isNull(),
                eq("CREATE"),
                isNull(),
                isNull(),
                isNull());
    }
}
