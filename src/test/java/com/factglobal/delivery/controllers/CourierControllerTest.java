package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourierControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierService courierService;

    @MockBean
    private UserService userService;

    @MockBean
    private CourierValidator courierValidator;

    @MockBean
    private Mapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Courier courier;
    private CourierDTO courierDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(3);
        user.setPhoneNumber("+79999999902");
        user.setPassword("100100100Gt");
        user.setCourier(courier);

        courier = new Courier();
        courier.setId(1);
        courier.setName("John");
        courier.setInn("123412341234");
        courier.setPhoneNumber("+79999999902");
        courier.setEmail("courier6@gmail.com");
        courier.setCourierStatus(Courier.Status.FREE);
        courier.setUser(user);

        courierDTO = new CourierDTO();
        courierDTO.setName("John");
        courierDTO.setInn("123412341234");
        courierDTO.setPhoneNumber("+79999999902");
        courierDTO.setEmail("courier6@gmail.com");
        courierDTO.setCourierStatus(Courier.Status.FREE);
    }

    @Nested
    class getCourierTests {
        @Test
        void getCourier_CourierRole_ReturnsCourier() throws Exception {
            when(courierService.findCourierByPhoneNumber(user.getPhoneNumber())).thenReturn(courier);
            when(mapper.convertToCourierDTO(courier)).thenReturn(courierDTO);

            var result = mockMvc.perform(get("/couriers")
                    .with(user("+79999999902").roles("COURIER"))
                    .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(courierDTO)));
            verify(courierService, times(1)).findCourierByPhoneNumber(user.getPhoneNumber());
            verify(mapper, times(1)).convertToCourierDTO(courier);
        }

        @Test
        void getCourier_AdminRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(get("/couriers")
                    .with(user("+79999999902").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class editCourierTests {
        @Test
        void editCourier_CourierRole_InvalidInput_ReturnsResponseOkAndCourier() throws Exception {
            when(mapper.convertToCourier(courierDTO)).thenReturn(courier);
            when(userService.findByPhoneNumber(user.getPhoneNumber()))
                    .thenReturn(Optional.of(user));
            when(courierService.findCourierUserId(user.getId()))
                    .thenReturn(courier.getId());
            when(userService.editCourier(courier, user.getId()))
                    .thenReturn(ResponseEntity.ok(courier));
            doNothing().when(courierValidator).validate(eq(courier), any(BindingResult.class));

            var result = mockMvc.perform(put("/couriers")
                    .with(user("+79999999902").roles("COURIER"))
                    .content(objectMapper.writeValueAsString(courierDTO))
                    .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(courier)));
            verify(mapper, times(1)).convertToCourier(courierDTO);
            verify(userService, times(1)).findByPhoneNumber(user.getPhoneNumber());
            verify(courierService, times(1)).findCourierUserId(user.getId());
            verify(courierValidator, times(1)).validate(eq(courier), any(BindingResult.class));
            verify(userService, times(1)).editCourier(courier, user.getId());
        }

        @Test
        void editCourier_AdminRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(put("/couriers")
                    .with(user("+79999999902").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class deleteCourierTests {
        @Test
        void editCourier_CourierRole_ReturnsResponseOkAndCourier() throws Exception {
            String response = "User with phone number:" + user.getPhoneNumber() + " is delete";

            when(userService.deleteUser(user.getId()))
                    .thenReturn(ResponseEntity.ok().body(response));
            when(userService.findByPhoneNumber(user.getPhoneNumber()))
                    .thenReturn(Optional.of(user));

            var result = mockMvc.perform(delete("/couriers")
                    .with(user("+79999999902").roles("COURIER"))
                    .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        void deleteCourier_AdminRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(delete("/couriers")
                    .with(user("+79999999902").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}