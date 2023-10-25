package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.JwtRequest;
import com.factglobal.delivery.dto.security.JwtResponse;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.util.JwtTokenUtils;
import com.factglobal.delivery.util.exception_handling.AuthError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createNewCourier(@RequestBody RegistrationCourierDto registrationCourierDto) {
        if (!registrationCourierDto.getPassword().equals(registrationCourierDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationCourierDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }


        User user = userService.createNewCourier(registrationCourierDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
    public ResponseEntity<?> createNewCustomer(@RequestBody RegistrationCustomerDto registrationCustomerDto) {
        if (!registrationCustomerDto.getPassword().equals(registrationCustomerDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationCustomerDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }


        User user = userService.createNewCustomer(registrationCustomerDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
}
