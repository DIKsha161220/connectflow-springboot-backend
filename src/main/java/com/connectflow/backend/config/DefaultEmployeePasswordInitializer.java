package com.connectflow.backend.config;

import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.entity.Role;
import com.connectflow.backend.repository.EmployeeRepository;
import com.connectflow.backend.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DefaultEmployeePasswordInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultEmployeePasswordInitializer.class);

    @Bean
    CommandLineRunner initializeDefaultData(
            RoleRepository roleRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                // 1. Ensure Default Roles Exist
                Role adminRole = safeGetRole(roleRepository, "Administrative", "Can manage institutes and access all CRM activities.");
                Role callingRole = safeGetRole(roleRepository, "Calling Team", "Can access assigned institutes and record calls.");
                Role fieldRole = safeGetRole(roleRepository, "On-field Employee", "Can access assigned institutes and manage visits and calls.");

                // 2. Ensure Default Employees Exist with password 'root'
                safeUpsertEmployee(employeeRepository, passwordEncoder, "Amit Patil", "amit.patil@example.com", adminRole);
                safeUpsertEmployee(employeeRepository, passwordEncoder, "Neha Shah", "neha.shah@example.com", callingRole);
                safeUpsertEmployee(employeeRepository, passwordEncoder, "Rohit More", "rohit.more@example.com", fieldRole);

                // Common aliases
                safeUpsertEmployee(employeeRepository, passwordEncoder, "Amit Patil", "amit@example.com", adminRole);
                safeUpsertEmployee(employeeRepository, passwordEncoder, "Neha Shah", "neha@example.com", callingRole);
                safeUpsertEmployee(employeeRepository, passwordEncoder, "Rohit More", "rohit@example.com", fieldRole);

                log.info("Default roles and employees check completed.");
            } catch (Exception e) {
                log.warn("Seeding initialization warning (non-fatal): {}", e.getMessage());
            }
        };
    }

    private Role safeGetRole(RoleRepository roleRepository, String roleName, String description) {
        try {
            return roleRepository.findByRoleNameIgnoreCase(roleName)
                    .orElseGet(() -> {
                        try {
                            Role role = new Role();
                            role.setRoleName(roleName);
                            role.setDescription(description);
                            role.setActive(true);
                            return roleRepository.save(role);
                        } catch (Exception e) {
                            return null;
                        }
                    });
        } catch (Exception e) {
            return null;
        }
    }

    private void safeUpsertEmployee(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            String fullName,
            String email,
            Role role) {
        try {
            Optional<Employee> existing = employeeRepository.findByEmailIgnoreCase(email);
            if (existing.isEmpty()) {
                if (role != null) {
                    Employee emp = new Employee();
                    emp.setFullName(fullName);
                    emp.setEmail(email);
                    emp.setPhone("9876543210");
                    emp.setPasswordHash(passwordEncoder.encode("root"));
                    emp.setRole(role);
                    emp.setActive(true);
                    employeeRepository.save(emp);
                }
            } else {
                Employee emp = existing.get();
                emp.setPasswordHash(passwordEncoder.encode("root"));
                emp.setActive(true);
                if (role != null && emp.getRole() == null) {
                    emp.setRole(role);
                }
                employeeRepository.save(emp);
            }
        } catch (Exception e) {
            log.debug("Notice for {}: {}", email, e.getMessage());
        }
    }
}
