package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.JwtRequest;
import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.services.AuthService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.AdminValidator;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
import com.factglobal.delivery.util.validation.PasswordsMatching;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Methods for registering and authenticating users")
public class AuthController {
    private final AuthService authService;
    private final PasswordsMatching passwordsMatching;
    private final CustomerValidator customerValidator;
    private final CourierValidator courierValidator;
    private final AdminValidator adminValidator;
    private final Mapper mapper;

    @Operation(summary = "User authentication and getting a JWT token")
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @Operation(summary = "Registering a new Courier user")
    @PostMapping("/registration/courier")
    public ResponseEntity<UserDto> createNewCourier(@RequestBody @Valid RegistrationCourierDto registrationCourierDto,
                                                    BindingResult bindingResult) {
        passwordsMatching.validate(registrationCourierDto, bindingResult);
        courierValidator.validate(mapper.convertToCourier(registrationCourierDto), bindingResult);
        ErrorValidation.message(bindingResult);

        return authService.createNewCourier(registrationCourierDto);
    }

    @Operation(summary = "Registering a new Customer user")
    @PostMapping("/registration/customer")
    public ResponseEntity<?> createNewCustomer(@RequestBody @Valid RegistrationCustomerDto registrationCustomerDto,
                                               BindingResult bindingResult) {
        passwordsMatching.validate(registrationCustomerDto, bindingResult);
        customerValidator.validate(mapper.convertToCustomer(registrationCustomerDto), bindingResult);
        ErrorValidation.message(bindingResult);

        return authService.createNewCustomer(registrationCustomerDto);
    }

    @Operation(summary = "Registering a new Admin user")
    @PostMapping("/registration/admin")
    public ResponseEntity<?> createNewAdmin(@RequestBody @Valid RegistrationAdminDTO registrationAdminDTO,
                                            BindingResult bindingResult) {
        passwordsMatching.validate(registrationAdminDTO, bindingResult);
        adminValidator.validate(registrationAdminDTO, bindingResult);
        ErrorValidation.message(bindingResult);

        return authService.createNewAdmin(registrationAdminDTO);
    }
}