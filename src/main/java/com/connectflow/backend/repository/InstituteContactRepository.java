package com.connectflow.backend.repository;

import com.connectflow.backend.entity.InstituteContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstituteContactRepository extends JpaRepository<InstituteContact, Long> {
    List<InstituteContact> findAllByInstitute_InstituteIdOrderByContactName(Long instituteId);
}
