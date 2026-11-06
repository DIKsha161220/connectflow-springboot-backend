package com.connectflow.backend.repository;

import com.connectflow.backend.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstituteRepository extends JpaRepository<Institute, Long> {
    List<Institute> findAllByAssignedEmployee_EmployeeIdOrderByInstituteName(Long employeeId);
}
