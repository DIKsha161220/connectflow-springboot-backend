package com.connectflow.backend.repository;

import com.connectflow.backend.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByEmployee_EmployeeIdOrderByVisitDatetimeDesc(Long employeeId);

    List<Visit> findAllByInstitute_InstituteIdOrderByVisitDatetimeDesc(Long instituteId);

    List<Visit> findAllByOrderByVisitDatetimeDesc();
}
