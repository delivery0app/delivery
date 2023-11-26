package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.*;
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


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<?> createAuthToken(JwtRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhoneNumber(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getPhoneNumber());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<UserDto> createNewCourier(RegistrationCourierDto registrationCourierDto) {
        User user = userService.createNewCourier(registrationCourierDto);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }

    public ResponseEntity<UserDto> createNewCustomer(RegistrationCustomerDto registrationCustomerDto) {
        User user = userService.createNewCustomer(registrationCustomerDto);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }

    public ResponseEntity<UserDto> createNewAdmin(RegistrationAdminDTO registrationAdminDTO) {
        User user = userService.createNewAdmin(registrationAdminDTO);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
}
