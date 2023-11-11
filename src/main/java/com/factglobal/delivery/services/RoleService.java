package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Role;
import com.factglobal.delivery.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole(String role) {
        return roleRepository.findByName(role).get();
    }
}
