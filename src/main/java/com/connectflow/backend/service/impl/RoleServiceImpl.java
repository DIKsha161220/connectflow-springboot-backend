package com.connectflow.backend.service.impl;

import com.connectflow.backend.dto.RoleResponse;
import com.connectflow.backend.repository.RoleRepository;
import com.connectflow.backend.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream().map(DtoMapper::role).toList();
    }
}
