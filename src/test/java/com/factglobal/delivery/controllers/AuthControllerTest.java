package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.*;
import com.factglobal.delivery.services.AuthService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.validation.AdminValidator;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
import com.factglobal.delivery.util.validation.PasswordsMatching;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordsMatching passwordsMatching;

    @MockBean
    private CourierValidator courierValidator;

    @MockBean
    private CustomerValidator customerValidator;

    @MockBean
    private AdminValidator adminValidator;

    @Autowired
    private Mapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistrationCourierDto registrationCourierDto;
    private RegistrationCustomerDto registrationCustomerDto;
    private RegistrationAdminDTO registrationAdminDTO;
    private JwtRequest jwtRequest;

    @BeforeEach
    void setUp() {
        registrationCourierDto = new RegistrationCourierDto();
        registrationCourierDto.setName("Test");
        registrationCourierDto.setEmail("courier6@gmail.com");
        registrationCourierDto.setPhoneNumber("+79999999988");
        registrationCourierDto.setInn("123412341236");
        registrationCourierDto.setPassword("100100100Gt");
        registrationCourierDto.setConfirmPassword("100100100Gt");

        registrationCustomerDto = new RegistrationCustomerDto();
        registrationCustomerDto.setName("John");
        registrationCustomerDto.setEmail("customer@gmail.com");
        registrationCustomerDto.setPhoneNumber("+79999999988");
        registrationCustomerDto.setPassword("100100100Gt");
        registrationCustomerDto.setConfirmPassword("100100100Gt");

        registrationAdminDTO = new RegistrationAdminDTO();
        registrationAdminDTO.setPhoneNumber("+79999999988");
        registrationAdminDTO.setPassword("100100100Gt");
        registrationAdminDTO.setConfirmPassword("100100100Gt");

        jwtRequest = new JwtRequest();
        jwtRequest.setPhoneNumber("+79999999988");
        jwtRequest.setPassword("100100100Gt");
    }

    @Test
    void createNewCourier_ValidInput_ReturnsResponseEntityOkAndUserDto() throws Exception {
        UserDto mockUser = new UserDto(1, registrationCourierDto.getPhoneNumber());

        when(authService.createNewCourier(registrationCourierDto))
                .thenReturn(ResponseEntity.ok((mockUser)));
        doNothing().when(passwordsMatching).validate(eq(registrationCourierDto), any(BindingResult.class));
        doNothing().when(courierValidator).validate(eq(mapper.convertToCourier(registrationCourierDto)), any(BindingResult.class));

        var result = mockMvc.perform(post("/registration/courier")
                        .content(objectMapper.writeValueAsString(registrationCourierDto))
                        .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockUser)));
        verify(authService, times(1)).createNewCourier(registrationCourierDto);
        verify(passwordsMatching, times(1)).validate(eq(registrationCourierDto), any(BindingResult.class));
        verify(courierValidator, times(1)).validate(eq(mapper.convertToCourier(registrationCourierDto)), any(BindingResult.class));
    }

    @Test
    void createNewCustomer_ValidInput_ReturnsResponseEntityOkAndUserDto() throws Exception {
        UserDto mockUser = new UserDto(1, registrationCustomerDto.getPhoneNumber());

        when(authService.createNewCustomer(registrationCustomerDto))
                .thenReturn(ResponseEntity.ok((mockUser)));
        doNothing().when(passwordsMatching).validate(eq(registrationCustomerDto), any(BindingResult.class));
        doNothing().when(customerValidator).validate(eq(mapper.convertToCustomer(registrationCustomerDto)), any(BindingResult.class));

        var result = mockMvc.perform(post("/registration/customer")
                        .content(objectMapper.writeValueAsString(registrationCustomerDto))
                        .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockUser)));
        verify(authService, times(1)).createNewCustomer(registrationCustomerDto);
        verify(passwordsMatching, times(1)).validate(eq(registrationCustomerDto), any(BindingResult.class));
        verify(customerValidator, times(1)).validate(eq(mapper.convertToCustomer(registrationCustomerDto)), any(BindingResult.class));
    }

    @Test
    void createNewAdmin_ValidInput_ReturnsResponseEntityOkAndUserDto() throws Exception {
        UserDto mockUser = new UserDto(1, registrationAdminDTO.getPhoneNumber());

        when(authService.createNewAdmin(registrationAdminDTO))
                .thenReturn(ResponseEntity.ok((mockUser)));
        doNothing().when(passwordsMatching).validate(eq(registrationAdminDTO), any(BindingResult.class));
        doNothing().when(adminValidator).validate(eq(registrationAdminDTO), any(BindingResult.class));

        var result = mockMvc.perform(post("/registration/admin")
                        .content(objectMapper.writeValueAsString(registrationAdminDTO))
                        .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockUser)));
        verify(authService, times(1)).createNewAdmin(registrationAdminDTO);
        verify(passwordsMatching, times(1)).validate(eq(registrationAdminDTO), any(BindingResult.class));
        verify(adminValidator, times(1)).validate(eq(registrationAdminDTO), any(BindingResult.class));
    }

    @Test
    void createAuthToken_ValidInput_ReturnsResponseEntityOk() throws Exception {
        when(authService.createAuthToken(jwtRequest)).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(jwtRequest)));

        result.andDo(print())
                .andExpect(status().isOk());

        verify(authService, times(1)).createAuthToken(jwtRequest);
    }
}
