package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomerServiceTest {
    @Autowired
    private CustomerService customerService;
    @MockBean
    private CustomerRepository customerRepository;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1);
        customer.setName("Customer_Name");
        customer.setPhoneNumber("+79999999999");
        customer.setEmail("customer@gmail.com");
    }

    @AfterEach
    void tearDown() {
        customer = null;
    }

    @Test
    void shouldThrowExceptionIfCustomerNotFound() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> customerService.findCustomerByUserId(0)),
                () -> assertThrows(EntityNotFoundException.class, () -> customerService.findCustomer(0)),
                () -> assertThrows(NoSuchElementException.class, () -> customerService.findAllCustomers()),
                () -> assertThrows(EntityNotFoundException.class, () -> customerService.findCustomerByPhoneNumber("dummy"))
        );
    }

    @Test
    void findCustomerByUserId_ValidUserId_ReturnsCustomerId() {
        when(customerRepository.findCustomerByUserId(1)).thenReturn(Optional.of(customer));

        Integer result = customerService.findCustomerByUserId(1);

        assertThat(result).isEqualTo(customer.getId());
        verify(customerRepository, times(1)).findCustomerByUserId(1);
    }

    @Test
    void findCustomer_ValidCustomerId_ReturnsCustomer() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomer(1);

        assertThat(result.getEmail()).isEqualTo(customer.getEmail());
        verify(customerRepository, times(1)).findById(1);
    }

    @Test
    void findAllCustomers_ReturnsListOfCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<Customer> result = customerService.findAllCustomers();

        assertThat(result.get(0).getEmail()).isEqualTo(customer.getEmail());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void saveAndFlush_ValidInput_SavesAndFlushesCustomer() {
        when(customerRepository.saveAndFlush(customer)).thenReturn(customer);

        customerService.saveAndFlush(customer);

        verify(customerRepository, times(1)).saveAndFlush(customer);
    }

    @Test
    void findCustomerByPhoneNumber_ValidPhoneNumber_ReturnsCustomer() {
        when(customerRepository.findCustomerByPhoneNumber("+79999999999")).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomerByPhoneNumber("+79999999999");

        assertThat(result.getEmail()).isEqualTo(customer.getEmail());
        verify(customerRepository, times(1)).findCustomerByPhoneNumber("+79999999999");
    }

    @Test
    void findCustomerByEmail_ValidEmail_ReturnsCustomer() {
        when(customerRepository.findCustomerByEmail("customer@gmail.com")).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomerByEmail("customer@gmail.com");

        assertThat(result.getEmail()).isEqualTo(customer.getEmail());
        verify(customerRepository, times(1)).findCustomerByEmail("customer@gmail.com");
    }
}