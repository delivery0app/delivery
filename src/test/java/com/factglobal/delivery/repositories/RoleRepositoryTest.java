package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByName_ValidInput_ReturnsRole() {
        Optional<Role> result = roleRepository.findByName("ROLE_ADMIN");
        assertTrue(result.isPresent());
    }

    @Test
    void findByName_NotFound_ReturnEmptyRole() {
        Optional<Role> result = roleRepository.findByName("dummy");
        assertTrue(result.isEmpty());
    }
}