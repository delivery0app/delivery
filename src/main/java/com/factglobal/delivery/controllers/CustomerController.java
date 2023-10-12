package com.factglobal.delivery.controllers;

import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<HttpStatus> addCustomer(@RequestBody Customer customer) {
        customerService.saveCustomer(customer);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable("id") int id) {
        return customerService.getCustomer(id);
    }

    @GetMapping()
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping
    public ResponseEntity<HttpStatus> editCustomer(@RequestBody Customer customer) {
        customerService.saveCustomer(customer);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") int id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
