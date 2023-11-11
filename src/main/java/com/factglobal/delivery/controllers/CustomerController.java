package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
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

    @GetMapping
    public CustomerDTO getCustomer(Principal principal) {
        return customerService.findCustomerDTOByPhoneNumber(principal.getName());
    }

    @PutMapping
    public ResponseEntity<?> editCustomer(@RequestBody @Valid CustomerDTO customerDTO,
                                                   BindingResult bindingResult,
                                                   Principal principal) {
        ResponseEntity<?> response = userService.editCustomer(customerDTO, userService.findByPhoneNumber(principal.getName()).orElse(null).getId(), bindingResult);
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
