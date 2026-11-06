package com.connectflow.backend.service;

import com.connectflow.backend.dto.RoleResponse;

import java.util.List;

public interface RoleService {
    List<RoleResponse> findAll();
}
