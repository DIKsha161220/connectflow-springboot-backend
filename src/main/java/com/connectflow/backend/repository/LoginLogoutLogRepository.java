package com.connectflow.backend.repository;

import com.connectflow.backend.entity.LoginLogoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginLogoutLogRepository extends JpaRepository<LoginLogoutLog, Long> {
    List<LoginLogoutLog> findAllByOrderByLoginTimeDesc();
    Optional<LoginLogoutLog> findFirstByEmailAndLogoutTimeIsNullOrderByLoginTimeDesc(String email);
    List<LoginLogoutLog> findByEmailOrderByLoginTimeDesc(String email);
}
