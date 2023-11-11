package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.*;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.util.JwtTokenUtils;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.exception_handling.AuthError;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
import com.factglobal.delivery.util.validation.PasswordsMatchingCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordsMatchingCheck passwordsMatchingCheck;
    private final CustomerValidator customerValidator;
    private final CourierValidator courierValidator;
    private final Mapper mapper;

    public ResponseEntity<?> createAuthToken(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhoneNumber(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getPhoneNumber());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createNewCourier(RegistrationCourierDto registrationCourierDto, BindingResult bindingResult) {
        passwordsMatchingCheck.validate(registrationCourierDto, bindingResult);
        courierValidator.validate(mapper.convertToCourier(registrationCourierDto), bindingResult);
        ErrorValidation.message(bindingResult);

        User user = userService.createNewCourier(registrationCourierDto);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
    public ResponseEntity<?> createNewCustomer(RegistrationCustomerDto registrationCustomerDto, BindingResult bindingResult) {
        passwordsMatchingCheck.validate(registrationCustomerDto, bindingResult);
        customerValidator.validate(mapper.convertToCustomer(registrationCustomerDto), bindingResult);
        ErrorValidation.message(bindingResult);

        User user = userService.createNewCustomer(registrationCustomerDto);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }

    public ResponseEntity<?> createNewAdmin(RegistrationAdminDTO registrationAdminDTO, BindingResult bindingResult) {
        passwordsMatchingCheck.validate(registrationAdminDTO, bindingResult);
        ErrorValidation.message(bindingResult);

        User user = userService.createNewAdmin(registrationAdminDTO);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
}
