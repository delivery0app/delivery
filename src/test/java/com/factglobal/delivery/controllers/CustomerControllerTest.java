package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.validation.CustomerValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.descriptor.web.WebXml;
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

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomerValidator customerValidator;

    @MockBean
    private Mapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;
    private CustomerDTO customerDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(3, "+79999999902", "100100100Gt", customer, false, null, null);
        customer = new Customer(1, "John", "+79999999902", "customer@gmail.com", null, user);
        customerDTO = new CustomerDTO("John", "+79999999902", "customer@gmail.com");
    }

    @Nested
    class getCustomerTest {
        @Test
        void getCustomer_CustomerRole_ReturnsCustomer() throws Exception {
            when(customerService.findCustomerByPhoneNumber(user.getPhoneNumber()))
                    .thenReturn(customer);
            when(mapper.convertToCustomerDTO(customer))
                    .thenReturn(customerDTO);

            mockMvc.perform(get("/customers")
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(customerDTO)));

            verify(customerService, times(1)).findCustomerByPhoneNumber(user.getPhoneNumber());
            verify(mapper, times(1)).convertToCustomerDTO(customer);
        }

        @Test
        void getCustomer_AdminRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            mockMvc.perform(get("/customers")
                            .with(user("+79999999902").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class editCustomerTest {
        @Test
        void editCustomer_CustomerRole_ValidInput_ReturnsResponseOkAndCustomer() throws Exception {
            when(mapper.convertToCustomer(customerDTO)).thenReturn(customer);
            when(userService.findByPhoneNumber(user.getPhoneNumber()))
                    .thenReturn(Optional.of(user));
            when(customerService.findCustomerByUserId(user.getId()))
                    .thenReturn(customer.getId());
            doNothing().when(customerValidator).validate(eq(customer), any(BindingResult.class));
            when(userService.editCustomer(customer, user.getId()))
                    .thenReturn(ResponseEntity.ok(customer));

            mockMvc.perform(put("/customers")
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .content(objectMapper.writeValueAsString(customerDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(customer)));

            verify(mapper, times(1)).convertToCustomer(customerDTO);
            verify(userService, times(1)).findByPhoneNumber(user.getPhoneNumber());
            verify(customerService, times(1)).findCustomerByUserId(user.getId());
            verify(customerValidator, times(1)).validate(eq(customer), any(BindingResult.class));
            verify(userService, times(1)).editCustomer(customer, user.getId());
        }

        @Test
        void editCustomer_AdminRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            mockMvc.perform(put("/customers")
                            .with(user("+79999999902").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class deleteCustomerTest {
        @Test
        void deleteCustomer_CustomerRole_ReturnsResponseOkAndCustomer() throws Exception {
            String response = "User with phone number:" + user.getPhoneNumber() + " is delete";

            when(userService.deleteUser(user.getId()))
                    .thenReturn(ResponseEntity.ok().body(response));
            when(userService.findByPhoneNumber(user.getPhoneNumber()))
                    .thenReturn(Optional.of(user));

            mockMvc.perform(delete("/customers")
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(response));
        }

        @Test
        void deleteCustomer_AdminRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            mockMvc.perform(delete("/customers")
                            .with(user("+79999999902").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}