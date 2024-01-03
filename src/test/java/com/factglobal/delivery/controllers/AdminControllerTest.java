package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
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
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierService courierService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private UserService userService;

    @MockBean
    private CourierValidator courierValidator;

    @MockBean
    private CustomerValidator customerValidator;

    @MockBean
    private Mapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Courier courier;
    private CourierDTO courierDTO;
    private Customer customer;
    private CustomerDTO customerDTO;

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

        customer = new Customer();
        customer.setId(1);
        customer.setName("John");
        customer.setPhoneNumber("+79999999902");
        customer.setEmail("customer@gmail.com");
        customer.setUser(user);

        customerDTO = new CustomerDTO();
        customerDTO.setName("John");
        customerDTO.setPhoneNumber("+79999999902");
        customerDTO.setEmail("customer@gmail.com");
    }

    @Nested
    class blockUserTests {
        @Test
        void blockUser_AdminRole_ValidInput_ReturnsResponseEntityOk() throws Exception{
            when(userService.blockUser(user.getId()))
                    .thenReturn(ResponseEntity.ok().build());

            var result = mockMvc.perform(post("/admins/users/{user_id}/block", user.getId())
                            .with(user("+79999999902").roles("ADMIN")));

            result.andDo(print())
                    .andExpect(status().isOk());
            verify(userService, times(1)).blockUser(user.getId());
        }

        @Test
        void blockUser_CustomerRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(post("/admins/users/{user_id}/block", user.getId())
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class unblockUserTests {
        @Test
        void unblockUser_AdminRole_ValidInput_ReturnsResponseEntityOk() throws Exception{
            when(userService.unblockUser(user.getId()))
                    .thenReturn(ResponseEntity.ok().build());

            var result = mockMvc.perform(post("/admins/users/{user_id}/unblock", user.getId())
                            .with(user("+79999999902").roles("ADMIN")));

            result.andDo(print())
                    .andExpect(status().isOk());
            verify(userService, times(1)).unblockUser(user.getId());
        }

        @Test
        void unblockUser_CustomerRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(post("/admins/users/{user_id}/unblock", user.getId())
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class deleteUserTests {
        @Test
        void deleteUser_AdminRole_ValidInput_ReturnsResponseEntityOkAndString() throws Exception{
            String response = "User with phone number:" + user.getPhoneNumber() + " is delete";

            when(userService.deleteUser(user.getId()))
                    .thenReturn(ResponseEntity.ok(response));

            var result = mockMvc.perform(delete("/admins/{user_id}", user.getId())
                            .with(user("+79999999902").roles("ADMIN")));

            result.andDo(print())
                    .andExpect(status().isOk());
            verify(userService, times(1)).deleteUser(user.getId());
        }

        @Test
        void deleteUser_CustomerRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(delete("/admins/{user_id}", user.getId())
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class editCourierTests {
        @Test
        void editCourier_AdminRole_ValidInput_ReturnsResponseEntityOkAndCourier() throws Exception{
            when(mapper.convertToCourier(courierDTO)).thenReturn(courier);
            when(courierService.findCourierUserId(user.getId()))
                    .thenReturn(courier.getId());
            doNothing().when(courierValidator).validate(eq(courier), any(BindingResult.class));
            when(userService.editCourier(courier, user.getId()))
                    .thenReturn(ResponseEntity.ok(courier));

            var result = mockMvc.perform(put("/admins/couriers/{user_id}", user.getId())
                            .with(user("+79999999902").roles("ADMIN"))
                            .content(objectMapper.writeValueAsString(courierDTO))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(courier)));
            verify(mapper, times(1)).convertToCourier(courierDTO);
            verify(courierService, times(1)).findCourierUserId(user.getId());
            verify(courierValidator, times(1)).validate(eq(courier), any(BindingResult.class));
            verify(userService, times(1)).editCourier(courier, user.getId());
        }

        @Test
        void editCourier_CustomerRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(put("/admins/couriers/{user_id}", user.getId())
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class editCustomerTests {
        @Test
        void editCustomer_AdminRole_ValidInput_ReturnsResponseEntityOkAndCustomer() throws Exception{
            when(mapper.convertToCustomer(customerDTO))
                    .thenReturn(customer);
            when(customerService.findCustomerByUserId(user.getId()))
                    .thenReturn(customer.getId());
            doNothing().when(customerValidator).validate(eq(customer), any(BindingResult.class));
            when(userService.editCustomer(customer, user.getId()))
                    .thenReturn(ResponseEntity.ok(customer));

            var result = mockMvc.perform(put("/admins/customers/{user_id}", user.getId())
                            .with(user("+79999999902").roles("ADMIN"))
                            .content(objectMapper.writeValueAsString(customerDTO))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(customer)));
            verify(mapper, times(1)).convertToCustomer(customerDTO);
            verify(customerService, times(1)).findCustomerByUserId(user.getId());
            verify(customerValidator, times(1)).validate(eq(customer), any(BindingResult.class));
            verify(userService, times(1)).editCustomer(customer, user.getId());
        }

        @Test
        void editCustomer_CourierRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(put("/admins/customers/{user_id}", user.getId())
                            .with(user("+79999999902").roles("COURIER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class getAllCouriersTests {
        @Test
        void getAllCouriers_AdminRole_ValidInput_ReturnsAllCouriersDTO() throws Exception {
            List<Courier> courierList = Collections.singletonList(courier);
            List<CourierDTO> courierDTOList = Collections.singletonList(courierDTO);

            when(courierService.findAllCouriers())
                    .thenReturn(courierList);
            when(mapper.convertToCourierDTO(courier))
                    .thenReturn(courierDTO);

            var result = mockMvc.perform(get("/admins/couriers")
                            .with(user("+79999999902").roles("ADMIN")));

            result.andDo(print())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(courierDTOList)));
            verify(courierService, times(1)).findAllCouriers();
            verify(mapper, times(courierList.size())).convertToCourierDTO(courier);
        }

        @Test
        void getAllCouriers_CustomerRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(get("/admins/couriers")
                            .with(user("+79999999902").roles("COURIER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class getAllCustomersTests {
        @Test
        void getAllCustomer_AdminRole_ValidInput_ReturnsAllCustomersDTO() throws Exception {
            List<Customer> customersList = Collections.singletonList(customer);
            List<CustomerDTO> customerDTOList = Collections.singletonList(customerDTO);

            when(customerService.findAllCustomers())
                    .thenReturn(customersList);
            when(mapper.convertToCustomerDTO(customer))
                    .thenReturn(customerDTO);

            var result = mockMvc.perform(get("/admins/customers")
                            .with(user("+79999999902").roles("ADMIN")));

            result.andDo(print())
                    .andExpect(content().json(
                            objectMapper.writeValueAsString(customerDTOList)));
            verify(customerService, times(1)).findAllCustomers();
            verify(mapper, times(customerDTOList.size())).convertToCustomerDTO(customer);
        }

        @Test
        void getAllCouriers_CustomerRole_WhenUnauthorized_ThrowsForbidden() throws Exception {
            var result = mockMvc.perform(get("/admins/customers")
                            .with(user("+79999999902").roles("CUSTOMER"))
                            .contentType(MediaType.APPLICATION_JSON));

            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}