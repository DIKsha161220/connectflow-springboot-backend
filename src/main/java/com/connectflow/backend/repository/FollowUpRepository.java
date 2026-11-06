package com.connectflow.backend.repository;

import com.connectflow.backend.entity.FollowUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    long countByFollowUpStatus(String followUpStatus);
}
