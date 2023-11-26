package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Integer findCustomerByUserId(int userId) {
        return customerRepository.findCustomerByUserId(userId)
                .orElseThrow((() -> new EntityNotFoundException("User with id: " + userId + " was not found")))
                .getId();
    }

    public Customer findCustomer(int id) {
        return customerRepository.findById(id)
                .orElseThrow((() -> new EntityNotFoundException("Customer with id: " + id + " was not found")));
    }

    public List<Customer> findAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        if (customers.isEmpty())
            throw new NoSuchElementException("No customer has been registered yet");

        return customers;
    }

    public void saveAndFlush(Customer customer) {
        customerRepository.saveAndFlush(customer);
    }


    public Customer findCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findCustomerByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("Customer with number: " + phoneNumber + " was not found"));
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email).orElse(null);
    }
}
