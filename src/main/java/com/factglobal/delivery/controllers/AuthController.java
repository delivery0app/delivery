package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.security.JwtRequest;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/registration/courier")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationCourierDto registrationCourierDto) {
        return authService.createNewCourier(registrationCourierDto);
    }
    @PostMapping("/registration/customer")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationCustomerDto registrationCustomerDto) {
        return authService.createNewCustomer(registrationCustomerDto);
    }
}