package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.User;
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
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setPhoneNumber("+79999999999");
        user.setPassword("SecureP@ss2");

        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        user = null;
        userRepository.deleteAll();
    }

    @Test
    void findByPhoneNumber_ValidInput_ReturnsUserByPhoneNumber() {
        Optional<User> result = userRepository.findByPhoneNumber(user.getPhoneNumber());
        assertThat(result.get().getPhoneNumber()).isEqualTo(user.getPhoneNumber());
    }

    @Test
    void findByPhoneNumber_NotFound_ReturnsEmptyOptional() {
        Optional<User> result = userRepository.findByPhoneNumber("dummy");
        assertTrue(result.isEmpty());
    }
}