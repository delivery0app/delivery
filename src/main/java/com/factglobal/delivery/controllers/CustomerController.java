package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CustomerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CustomerValidator customerValidator;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/info")
    public CustomerDTO getCustomer(Principal principal) {
        return convertToCustomerDTO(customerService.getCustomerByPhoneNumber(principal.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(this::convertToCustomerDTO)
                .toList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{user_id}")
//    public ResponseEntity<?> editCustomerByAdmin(@RequestBody @Valid CustomerDTO customerDTO,
//                                                   BindingResult bindingResult,
//                                                   @PathVariable("user_id") int id) {
//        Customer customer = convertToCustomer(customerDTO);
//        customer.setId(id);
//        customerValidator.validate(customer, bindingResult);
//        ErrorValidation.message(bindingResult);
//
//        return customerService.saveOrUpdate(customer);
//    }
//
//    @PreAuthorize("hasRole('CUSTOMER')")
//    @PutMapping
//    public ResponseEntity<?> editCustomer(@RequestBody @Valid CustomerDTO customerDTO,
//                                                   BindingResult bindingResult,
//                                                   Principal principal) {
//        Customer customer = convertToCustomer(customerDTO);
//        customer.setId(customerService.getCustomerByPhoneNumber(principal.getName()).getId());
//        customerValidator.validate(customer, bindingResult);
//        ErrorValidation.message(bindingResult);
//
//        return customerService.saveOrUpdate(customer);
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteCustomerByAdmin(@PathVariable("user_id") int id) {
        return userService.deleteUser(id);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping
    public ResponseEntity<?> deleteCustomer(Principal principal) {
        ResponseEntity<?> response = userService.deleteUser(userService.findByPhoneNumber(principal.getName()).orElse(null).getId());
        SecurityContextHolder.clearContext();

        return response;
    }

    private Customer convertToCustomer(CustomerDTO customerDTO) {
        return modelMapper.map(customerDTO, Customer.class);
    }

    private CustomerDTO convertToCustomerDTO(Customer customer) {
        return modelMapper.map(customer, CustomerDTO.class);
    }
}
