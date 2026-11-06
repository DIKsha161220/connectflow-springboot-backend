package com.connectflow.backend.config;

import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DefaultEmployeePasswordInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultEmployeePasswordInitializer.class);

    @Bean
    CommandLineRunner initializeDefaultPasswords(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            List<String> defaultEmails = List.of(
                    "amit.patil@example.com",
                    "neha.shah@example.com",
                    "rohit.more@example.com"
            );

            for (String email : defaultEmails) {
                employeeRepository.findByEmailIgnoreCase(email).ifPresent(employee -> {
                    if (employee.getPasswordHash() == null || employee.getPasswordHash().isBlank()) {
                        employee.setPasswordHash(passwordEncoder.encode("password123"));
                        employeeRepository.save(employee);
                        log.info("Initialized default password 'password123' for employee: {}", email);
                    }
                });
            }
        };
    }
}
