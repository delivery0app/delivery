package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.*;
import com.factglobal.delivery.models.Courier;
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

    public ResponseEntity<?> createNewCourier(RegistrationCourierDto registrationCourierDto) {
//        if (!registrationCourierDto.getPassword().equals(registrationCourierDto.getConfirmPassword())) {
//            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
//        }
//        if (userService.findByUsername(courier.getUsername()).isPresent()) {
//            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
//        }

        User user = userService.createNewCourier(registrationCourierDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
    public ResponseEntity<?> createNewCustomer(RegistrationCustomerDto registrationCustomerDto) {
//        if (!registrationCustomerDto.getPassword().equals(registrationCustomerDto.getConfirmPassword())) {
//            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
//        }
//        if (userService.findByUsername(registrationCustomerDto.getUsername()).isPresent()) {
//            return new ResponseEntity<>(new AuthError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
//        }

        User user = userService.createNewCustomer(registrationCustomerDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }

    public ResponseEntity<?> createNewAdmin(RegistrationAdminDTO registrationAdminDTO) {
        User user = userService.createNewAdmin(registrationAdminDTO);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
}
