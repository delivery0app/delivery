package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.repositories.CourierRepository;
import com.factglobal.delivery.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CourierServiceTest {
    @Autowired
    private CourierService courierService;
    @MockBean
    private CourierRepository courierRepository;
    Courier courier;


    @BeforeEach
    void setUp() {
        courier = new Courier();
        courier.setId(1);
        courier.setName("Courier_Name");
        courier.setPhoneNumber("+79999999999");
        courier.setEmail("courier@gmail.com");
        courier.setInn("123456789029");

    }

    @AfterEach
    void tearDown() {
        courier = null;
    }

    @Test
    void shouldThrowExceptionIfCourierNotFound() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> courierService.findCourier(0)),
                () -> assertThrows(EntityNotFoundException.class, () -> courierService.findCourierUserId(0)),
                () -> assertThrows(NoSuchElementException.class, () -> courierService.findAllCouriers()),
                () -> assertThrows(EntityNotFoundException.class, () -> courierService.findCourierByPhoneNumber("dummy"))
        );
    }

    @Test
    void findCourier_ValidCourierId_ReturnsCourier() {
        when(courierRepository.findById(1)).thenReturn(Optional.of(courier));

        Courier result = courierService.findCourier(1);

        assertThat(result.getEmail()).isEqualTo(courier.getEmail());
        verify(courierRepository, times(1)).findById(1);
    }

    @Test
    void findCourierIdByUserId_ValidUserId_ReturnsCourierId() {
        when(courierRepository.findCourierByUserId(1)).thenReturn(Optional.of(courier));

        Integer result = courierService.findCourierUserId(1);

        assertThat(result).isEqualTo(courier.getId());
        verify(courierRepository, times(1)).findCourierByUserId(1);
    }

    @Test
    void findAllCourier_ReturnsListOfCouriers() {
        when(courierRepository.findAll()).thenReturn(List.of(courier));

        List<Courier> result = courierService.findAllCouriers();

        assertThat(result.get(0).getEmail()).isEqualTo(courier.getEmail());
        verify(courierRepository, times(1)).findAll();
    }

    @Test
    void saveAndFlush_ValidInput_SavesAndFlushesCourier() {
        when(courierRepository.saveAndFlush(courier)).thenReturn(courier);

        courierService.saveAndFlush(courier);

        verify(courierRepository, times(1)).saveAndFlush(courier);
    }

    @Test
    void findCourierByEmail_ValidEmail_ReturnsCourier() {
        when(courierRepository.findCourierByEmail("courier@gmail.com")).thenReturn(Optional.of(courier));

        Courier result = courierService.findCourierByEmail("courier@gmail.com");

        assertThat(result.getEmail()).isEqualTo(courier.getEmail());
        verify(courierRepository, times(1)).findCourierByEmail("courier@gmail.com");
    }

    @Test
    void findCourierByPhoneNumber_ValidPhoneNumber_ReturnsCourier() {
        when(courierRepository.findCourierByPhoneNumber("+79999999999")).thenReturn(Optional.of(courier));

        Courier result = courierService.findCourierByPhoneNumber("+79999999999");

        assertThat(result.getEmail()).isEqualTo(courier.getEmail());
        verify(courierRepository, times(1)).findCourierByPhoneNumber("+79999999999");

    }

    @Test
    void findCourierByInn_ValidInn_ReturnsCourier() {
        when(courierRepository.findCourierByInn("123456789029")).thenReturn(Optional.of(courier));

        Courier result = courierService.findCourierByInn("123456789029");

        assertThat(result.getEmail()).isEqualTo(courier.getEmail());
        verify(courierRepository, times(1)).findCourierByInn("123456789029");
    }

    @Test
    void enrichCourier_ValidInput_SetsCourierStatusToFree() {
        courierService.enrichCourier(courier);

        assertEquals(Courier.Status.FREE, courier.getCourierStatus());
    }
}