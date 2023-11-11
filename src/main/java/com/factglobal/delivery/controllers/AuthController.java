package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.security.JwtRequest;
import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<?> createNewUser(@RequestBody @Valid RegistrationCourierDto registrationCourierDto,
                                           BindingResult bindingResult) {
        return authService.createNewCourier(registrationCourierDto, bindingResult);
    }

    @PostMapping("/registration/customer")
    public ResponseEntity<?> createNewUser(@RequestBody @Valid RegistrationCustomerDto registrationCustomerDto,
                                           BindingResult bindingResult) {
        return authService.createNewCustomer(registrationCustomerDto, bindingResult);
    }

    @PostMapping("/registration/admin")
    public ResponseEntity<?> createNewUser(@RequestBody @Valid RegistrationAdminDTO registrationAdminDTO,
                                         BindingResult bindingResult) {
        return authService.createNewAdmin(registrationAdminDTO, bindingResult);
    }
}