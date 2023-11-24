package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
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
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;

    Customer customer;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setPhoneNumber("+79999999999");
        user.setPassword("SecureP@ss2");
        userRepository.save(user);

        customer = new Customer();
        customer.setName("Customer_Name");
        customer.setPhoneNumber("+79999999999");
        customer.setEmail("customer@gmail.com");
        customer.setUser(user);

        customerRepository.save(customer);
    }

    @AfterEach
    void tearDown() {
        customer = null;
        user = null;
        customerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findCustomerByPhoneNumber_ValidInput_ReturnsCustomerByPhoneNumber() {
        Optional<Customer> result = customerRepository.findCustomerByPhoneNumber(customer.getPhoneNumber());
        assertThat(result.get().getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void findCustomerByPhoneNumber_NotFound_ReturnsEmptyOptional() {
        Optional<Customer> result = customerRepository.findCustomerByPhoneNumber("dummy");
        assertTrue(result.isEmpty());
    }

    @Test
    void findCustomerByEmail_ValidInput_ReturnsCustomerByEmail() {
        Optional<Customer> result = customerRepository.findCustomerByEmail(customer.getEmail());
        assertThat(result.get().getEmail()).isEqualTo(customer.getEmail());
        assertEquals(result.get().getEmail(), customer.getEmail());
    }

    @Test
    void findCustomerByEmail_NotFound_ReturnsEmptyOptional() {
        Optional<Customer> result = customerRepository.findCustomerByEmail("dummy");
        assertTrue(result.isEmpty());
    }

    @Test
    void findCustomerByUserId_ValidInput_ReturnsCustomerByUserId() {
        Optional<Customer> result = customerRepository.findCustomerByUserId(customer.getUser().getId());
        assertThat(result.get().getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void findCustomerByUserId_NotFound_ReturnsEmptyOptional() {
        Optional<Customer> result = customerRepository.findCustomerByUserId(0);
        assertTrue(result.isEmpty());
    }
}