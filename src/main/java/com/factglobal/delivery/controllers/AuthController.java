package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.security.JwtRequest;
import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.AuthService;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
import com.factglobal.delivery.util.validation.PasswordsMatchingCheck;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordsMatchingCheck passwordsMatchingCheck;
    private final CourierValidator courierValidator;
    private final CustomerValidator customerValidator;
    private final ModelMapper modelMapper;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/registration/courier")
    public ResponseEntity<?> createNewUser(@RequestBody @Valid RegistrationCourierDto registrationCourierDto,
                                           BindingResult bindingResult) {
        passwordsMatchingCheck.validate(registrationCourierDto, bindingResult);
        courierValidator.validate(convertToCourier(registrationCourierDto), bindingResult);
        ErrorValidation.message(bindingResult);

        return authService.createNewCourier(registrationCourierDto);
    }
    @PostMapping("/registration/customer")
    public ResponseEntity<?> createNewUser(@RequestBody @Valid RegistrationCustomerDto registrationCustomerDto,
                                           BindingResult bindingResult) {
        passwordsMatchingCheck.validate(registrationCustomerDto, bindingResult);
        customerValidator.validate(convertToCustomer(registrationCustomerDto), bindingResult);
        ErrorValidation.message(bindingResult);

        return authService.createNewCustomer(registrationCustomerDto);
    }

    @PostMapping("/registration/admin")
    public ResponseEntity<?> createAdmin(@RequestBody @Valid RegistrationAdminDTO registrationAdminDTO,
                                         BindingResult bindingResult) {
        passwordsMatchingCheck.validate(registrationAdminDTO, bindingResult);
        ErrorValidation.message(bindingResult);

        return authService.createNewAdmin(registrationAdminDTO);
    }

    private Courier convertToCourier(RegistrationCourierDto registrationCourierDto) {
        return modelMapper.map(registrationCourierDto, Courier.class);
    }

    private Customer convertToCustomer(RegistrationCustomerDto registrationCustomerDto) {
        return modelMapper.map(registrationCustomerDto, Customer.class);
    }
}