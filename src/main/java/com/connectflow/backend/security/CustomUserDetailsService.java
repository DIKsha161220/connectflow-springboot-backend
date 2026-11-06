package com.connectflow.backend.security;

import com.connectflow.backend.entity.Employee;
import com.connectflow.backend.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        if (!Boolean.TRUE.equals(employee.getActive())
                || !Boolean.TRUE.equals(employee.getRole().getActive())) {
            throw new UsernameNotFoundException("Invalid email or password");
        }
        return EmployeePrincipal.from(employee);
    }
}
