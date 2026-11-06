package com.connectflow.backend.security;

import com.connectflow.backend.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class EmployeePrincipal implements UserDetails {

    private final Long employeeId;
    private final String email;
    private final String passwordHash;
    private final String roleName;
    private final boolean active;

    private EmployeePrincipal(Employee employee) {
        this.employeeId = employee.getEmployeeId();
        this.email = employee.getEmail();
        this.passwordHash = employee.getPasswordHash() == null ? "" : employee.getPasswordHash();
        this.roleName = employee.getRole().getRoleName();
        this.active = Boolean.TRUE.equals(employee.getActive())
                && Boolean.TRUE.equals(employee.getRole().getActive());
    }

    public static EmployeePrincipal from(Employee employee) {
        return new EmployeePrincipal(employee);
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String normalized = roleName.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_|_$", "");
        return List.of(new SimpleGrantedAuthority("ROLE_" + normalized));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
