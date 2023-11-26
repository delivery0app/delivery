package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.UserDto;
import com.factglobal.delivery.dto.security.*;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.util.JwtTokenUtils;
import com.factglobal.delivery.util.exception_handling.AuthError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    private AuthService authService;
    @MockBean
    private JwtTokenUtils jwtTokenUtils;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserService userService;


    JwtRequest authRequest;
    UserDetails userDetails;

    @Test
    void createAuthToken_ValidJwtRequest_ReturnsGeneratedToken() {
        authRequest = new JwtRequest();
        authRequest.setPhoneNumber("+79999999999");
        authRequest.setPassword("SecureP@ss2");

        when(userService.loadUserByUsername(authRequest.getPhoneNumber())).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(userDetails)).thenReturn("generatedToken");

        ResponseEntity<?> result = authService.createAuthToken(authRequest);
        JwtResponse jwtResponse = (JwtResponse) result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jwtResponse).isInstanceOf(JwtResponse.class);
        verify(authenticationManager, times(1)).authenticate(any());
        verify(userService, times(1)).loadUserByUsername(authRequest.getPhoneNumber());
        verify(jwtTokenUtils, times(1)).generateToken(userDetails);
    }

    @Test
    void createAuthToken_InvalidCredentials_ReturnsUnauthorized() {
        authRequest = new JwtRequest();
        authRequest.setPhoneNumber("invalidPhoneNumber");
        authRequest.setPassword("invalidPassword");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> result = authService.createAuthToken(authRequest);
        AuthError authError = (AuthError) result.getBody();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isInstanceOf(AuthError.class);
        assertThat(authError.getMessage()).isEqualTo("Incorrect login or password");
        assertThat(result.getStatusCode().value()).isEqualTo(authError.getStatus());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void createNewCourier_ValidInput_ReturnsUserDto() {
        RegistrationCourierDto registrationCourierDto = new RegistrationCourierDto();
        registrationCourierDto.setName("Courier_Name");
        registrationCourierDto.setEmail("courier@gmail.com");
        registrationCourierDto.setInn("123456789029");
        registrationCourierDto.setPhoneNumber("+79999999999");
        registrationCourierDto.setPassword("SecureP@ss2");
        registrationCourierDto.setConfirmPassword("SecureP@ss2");
        User user = new User();
        user.setPhoneNumber(registrationCourierDto.getPhoneNumber());

        when(userService.createNewCourier(registrationCourierDto)).thenReturn(user);

        ResponseEntity<UserDto> result = authService.createNewCourier(registrationCourierDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(result.getBody());
        assertThat(result.getBody().getUsername()).isEqualTo(registrationCourierDto.getPhoneNumber());
        verify(userService, times(1)).createNewCourier(registrationCourierDto);
    }

    @Test
    void createNewCustomer_ValidInput_ReturnsUserDto() {
        RegistrationCustomerDto registrationCustomerDto = new RegistrationCustomerDto();
        registrationCustomerDto.setName("Customer_Name");
        registrationCustomerDto.setEmail("Customer@gmail.com");
        registrationCustomerDto.setPhoneNumber("+79999999999");
        registrationCustomerDto.setPassword("SecureP@ss2");
        registrationCustomerDto.setConfirmPassword("SecureP@ss2");
        User user = new User();
        user.setPhoneNumber(registrationCustomerDto.getPhoneNumber());

        when(userService.createNewCustomer(registrationCustomerDto)).thenReturn(user);

        ResponseEntity<UserDto> result = authService.createNewCustomer(registrationCustomerDto);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(result.getBody());
        assertThat(result.getBody().getUsername()).isEqualTo(registrationCustomerDto.getPhoneNumber());
        verify(userService, times(1)).createNewCustomer(registrationCustomerDto);
    }

    @Test
    void createNewAdmin_ValidInput_ReturnsUserDto() {
        RegistrationAdminDTO registrationAdminDTO = new RegistrationAdminDTO();
        registrationAdminDTO.setPhoneNumber("+79999999999");
        registrationAdminDTO.setPassword("SecureP@ss2");
        registrationAdminDTO.setConfirmPassword("SecureP@ss2");
        User user = new User();
        user.setPhoneNumber(registrationAdminDTO.getPhoneNumber());

        when(userService.createNewAdmin(registrationAdminDTO)).thenReturn(user);

        ResponseEntity<UserDto> result = authService.createNewAdmin(registrationAdminDTO);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(result.getBody());
        assertThat(result.getBody().getUsername()).isEqualTo(registrationAdminDTO.getPhoneNumber());
        verify(userService, times(1)).createNewAdmin(registrationAdminDTO);

    }
}