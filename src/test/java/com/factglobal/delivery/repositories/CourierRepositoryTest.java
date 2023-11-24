package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourierRepositoryTest {
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private UserRepository userRepository;
    Courier courier;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setPhoneNumber("+79999999999");
        user.setPassword("SecureP@ss2");
        userRepository.save(user);

        courier = new Courier();
        courier.setName("Courier_Name");
        courier.setPhoneNumber("+79999999424");
        courier.setEmail("courier@gmail.com");
        courier.setInn("123456789029");
        courier.setUser(user);

        courierRepository.save(courier);
    }

    @AfterEach
    void tearDown() {
        courier = null;
        user = null;
        courierRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findCourierByPhoneNumber_ValidInput_ReturnsCourierByPhoneNumber() {
        Optional<Courier> result = courierRepository.findCourierByPhoneNumber(courier.getPhoneNumber());
        assertThat(result.get().getInn()).isEqualTo(courier.getInn());
    }

    @Test
    void findCourierByPhoneNumber_NotFound_ReturnsEmptyOptional() {
        Optional<Courier> result = courierRepository.findCourierByPhoneNumber("dummy");
        assertTrue(result.isEmpty());
    }

    @Test
    void findCourierByEmail_ValidInput_ReturnsCourierByEmail() {
        Optional<Courier> result = courierRepository.findCourierByEmail(courier.getEmail());
        assertThat(result.get().getInn()).isEqualTo(courier.getInn());
    }

    @Test
    void findCourierByEmail_NotFound_ReturnsEmptyOptional() {
        Optional<Courier> result = courierRepository.findCourierByEmail("dummy");
        assertTrue(result.isEmpty());
    }

    @Test
    void findCourierByInn_ValidInput_ReturnsCourierByInn() {
        Optional<Courier> result = courierRepository.findCourierByInn(courier.getInn());
        assertThat(result.get().getInn()).isEqualTo(courier.getInn());
    }

    @Test
    void findCourierByInn_NotFound_ReturnsEmptyOptional() {
        Optional<Courier> result = courierRepository.findCourierByInn("dummy");
        assertTrue(result.isEmpty());
    }


    @Test
    void findCourierByUserId_ValidInput_ReturnsCourierByUserId() {
        Optional<Courier> result = courierRepository.findCourierByUserId(user.getId());
        assertThat(result.get().getInn()).isEqualTo(courier.getInn());
    }

    @Test
    void findCourierByUserId_NotFound_ReturnsEmptyOptional() {
        Optional<Courier> result = courierRepository.findCourierByUserId(0);
        assertTrue(result.isEmpty());
    }
}