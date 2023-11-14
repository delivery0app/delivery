package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CustomerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;
    private final CustomerValidator customerValidator;
    private final Mapper mapper;

    @GetMapping
    public CustomerDTO getCustomer(Principal principal) {
        return customerService.findCustomerDTOByPhoneNumber(principal.getName());
    }

    @PutMapping
    public ResponseEntity<?> editCustomer(@RequestBody @Valid CustomerDTO customerDTO,
                                          BindingResult bindingResult,
                                          Principal principal) {
        var customer = mapper.convertToCustomer(customerDTO);
        int userId = userService.findByPhoneNumber(principal.getName()).orElse(null).getId();
        customer.setId(customerService.findCustomerByUserId(userId));
        customerValidator.validate(customer, bindingResult);
        ErrorValidation.message(bindingResult);

        ResponseEntity<?> response = userService.editCustomer(customer, userId);
        SecurityContextHolder.clearContext();

        return response;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCustomer(Principal principal) {
        ResponseEntity<?> response = userService.deleteUser(userService.findByPhoneNumber(principal.getName()).orElse(null).getId());
        SecurityContextHolder.clearContext();

        return response;
    }
}
