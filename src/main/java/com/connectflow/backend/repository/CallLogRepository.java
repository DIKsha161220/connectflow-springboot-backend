package com.connectflow.backend.repository;

import com.connectflow.backend.entity.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {
    List<CallLog> findAllByInstitute_InstituteIdOrderByCallDatetimeDesc(Long instituteId);

    List<CallLog> findAllByEmployee_EmployeeIdOrderByCallDatetimeDesc(Long employeeId);

    List<CallLog> findAllByOrderByCallDatetimeDesc();
}
