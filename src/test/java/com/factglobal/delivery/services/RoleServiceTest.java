package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Role;
import com.factglobal.delivery.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RoleServiceTest {
    @Autowired
    private RoleService roleService;
    @MockBean
    private RoleRepository roleRepository;

    Role role;

    @Test
    void getUserRole_ValidRole_ReturnsRole() {
        role = new Role();
        role.setName("ROLE_COURIER");
        when(roleRepository.findByName("ROLE_COURIER")).thenReturn(Optional.of(role));

        Role result = roleService.getUserRole("ROLE_COURIER");

        assertNotNull(result);
        assertThat(result).isEqualTo(role);
        verify(roleRepository, times(1)).findByName("ROLE_COURIER");
    }
}