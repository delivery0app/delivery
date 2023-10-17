package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.util.exception_handling.ErrorMessage;
import com.factglobal.delivery.util.validation.CustomerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final ModelMapper modelMapper;
    private final CustomerValidator customerValidator;

    @PostMapping
    public ResponseEntity<HttpStatus> addCustomer(@RequestBody @Valid CustomerDTO customerDTO,
                                                  BindingResult bindingResult) {
        customerValidator.validate(convertToCustomer(customerDTO), bindingResult);
        ErrorMessage.errorMessage(bindingResult);
        customerService.saveCustomer(convertToCustomer(customerDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable("id") int id) {
        return convertToCustomerDTO(customerService.getCustomer(id));
    }

    @GetMapping()
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(this::convertToCustomerDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> editCustomer(@RequestBody @Valid CustomerDTO customerDTO,
                                                   BindingResult bindingResult,
                                                   @PathVariable("id") int id) {
        Customer customer = convertToCustomer(customerDTO);
        customer.setId(id);
        customerValidator.validate(customer, bindingResult);
        ErrorMessage.errorMessage(bindingResult);
        customerService.saveCustomer(customer);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") int id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Customer convertToCustomer(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }

    private CustomerDTO convertToCustomerDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }
}
