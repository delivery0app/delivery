package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer findById(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer getCustomer(int id) {
        return customerRepository.findById(id)
                .orElseThrow((() -> new EntityNotFoundException("Customer with id: " + id + " was not found")));
    }

//    public ResponseEntity<?> deleteCustomer(int id) {
//        String phoneNumber = findById(id).getPhoneNumber();
//        customerRepository.deleteById(id);
//
//        return ResponseEntity.ok().body("User with phone number:" + phoneNumber + " is delete");
//    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        if (customers.isEmpty())
            throw new NoSuchElementException("No customer has been registered yet");

        return customers;
    }

    public Customer getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findCustomerByPhoneNumber(phoneNumber).orElse(null);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email).orElse(null);
    }

    public void saveAndFlush(Customer customer) {
        customerRepository.saveAndFlush(customer);
    }

    public ResponseEntity<?> saveOrUpdate(Customer customer) {
        return ResponseEntity.ok(customerRepository.save(customer));
    }
}
