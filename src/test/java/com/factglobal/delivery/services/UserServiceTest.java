package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Role;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleService roleService;

    @MockBean
    private CourierService courierService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private PasswordEncoder passwordEncoder;


    User user;
    Role role;

    @BeforeEach
    void setUp() {
        role = new Role();

        user = new User();
        user.setId(1);
        user.setPhoneNumber("+79999999999");
        user.setPassword("SecureP@ss2");
        user.setBlock(true);
    }

    @AfterEach
    void tearDown() {
        user = null;
        role = null;
    }

    @Test
    void throwExceptionIfUserNotFound() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> userService.findById(0)),
                () -> assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("dummy"))
        );
    }

    @Test
    void findById_ValidUserId_ReturnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.findById(1);

        assertThat(result.getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void loadUserByUsername_ValidPhoneNumber_ReturnsUserDetails() {
        role.setName("ROLE_COURIER");
        user.setRoles(Set.of(role));
        when(userRepository.findByPhoneNumber("+79999999999")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("+79999999999");

        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getPassword()).isEqualTo(result.getPassword());
        assertThat(result.getAuthorities().size()).isEqualTo(1);
        assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_COURIER");
        verify(userRepository, times(1)).findByPhoneNumber(anyString());

    }

    @Test
    void createAndSaveUser_ValidInput_ReturnsSavedUser() {
        role.setName("ROLE_COURIER");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(roleService.getUserRole(anyString())).thenReturn(role);

        User result = userService.createAndSaveUser("+79999999999", "SecureP@ss2", role.getName());

        assertThat(result.getPhoneNumber()).isEqualTo("+79999999999");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getRoles().stream().findFirst()).isEqualTo(Optional.of(role));
        assertTrue(result.getBlock());
        verify(passwordEncoder, times(1)).encode("SecureP@ss2");
        verify(roleService, times(1)).getUserRole(role.getName());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void createNewCourier_ValidInput_ReturnsSavedUser() {
        role.setName("ROLE_COURIER");
        user.setRoles(Set.of(role));

        RegistrationCourierDto registrationCourierDto = new RegistrationCourierDto();
        registrationCourierDto.setName("Courier_Name");
        registrationCourierDto.setEmail("courier@gmail.com");
        registrationCourierDto.setInn("123456789029");
        registrationCourierDto.setPhoneNumber("+79999999999");
        registrationCourierDto.setPassword("SecureP@ss2");

        when(roleService.getUserRole(anyString())).thenReturn(role);
        when(userService.createAndSaveUser(registrationCourierDto.getPhoneNumber(), registrationCourierDto.getPassword(), "ROLE_COURIER")).thenReturn(user);
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(courierService).saveAndFlush(any(Courier.class));
        doNothing().when(courierService).enrichCourier(any(Courier.class));

        User result = userService.createNewCourier(registrationCourierDto);

        assertThat(result.getPhoneNumber()).isEqualTo(registrationCourierDto.getPhoneNumber());
        assertThat(result.getCourier().getEmail()).isEqualTo(registrationCourierDto.getEmail());
        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).save(any(User.class));
        verify(courierService, times(1)).saveAndFlush(any(Courier.class));
        verify(courierService, times(1)).enrichCourier(any(Courier.class));

    }

    @Test
    void createNewCustomer_ValidInput_ReturnsSavedUser() {
        role.setName("ROLE_CUSTOMER");
        user.setRoles(Set.of(role));

        RegistrationCustomerDto registrationCustomerDto = new RegistrationCustomerDto();
        registrationCustomerDto.setName("Customer_Name");
        registrationCustomerDto.setEmail("Customer@gmail.com");
        registrationCustomerDto.setPhoneNumber("+79999999999");
        registrationCustomerDto.setPassword("SecureP@ss2");

        when(roleService.getUserRole(role.getName())).thenReturn(role);
        when(userService.createAndSaveUser(registrationCustomerDto.getPhoneNumber(), registrationCustomerDto.getPassword(), "ROLE_CUSTOMER")).thenReturn(user);
        when(userRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(customerService).saveAndFlush(any(Customer.class));

        User result = userService.createNewCustomer(registrationCustomerDto);

        assertThat(result.getPhoneNumber()).isEqualTo(registrationCustomerDto.getPhoneNumber());
        assertThat(result.getCustomer().getEmail()).isEqualTo(registrationCustomerDto.getEmail());
        assertThat(result).isEqualTo(user);
        verify(userRepository, times(1)).save(any(User.class));
        verify(customerService, times(1)).saveAndFlush(any(Customer.class));

    }

    @Test
    void createNewAdmin_ValidInput_ReturnsSavedUser() {
        role.setName("ROLE_ADMIN");
        user.setRoles(Set.of(role));

        RegistrationAdminDTO registrationAdminDTO = new RegistrationAdminDTO();
        registrationAdminDTO.setPhoneNumber("+79999999999");
        registrationAdminDTO.setPassword("SecureP@ss2");

        when(roleService.getUserRole(role.getName())).thenReturn(role);
        when(userService.createAndSaveUser(registrationAdminDTO.getPhoneNumber(), registrationAdminDTO.getPassword(), "ROLE_ADMIN")).thenReturn(user);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        User result = userService.createNewAdmin(registrationAdminDTO);

        assertThat(result.getPhoneNumber()).isEqualTo(registrationAdminDTO.getPhoneNumber());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void editCourier_ValidInput_ReturnsOkResponse() {
        Courier courier = new Courier();
        courier.setPhoneNumber("+79999999111");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        doNothing().when(courierService).saveAndFlush(any(Courier.class));
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> result = userService.editCourier(courier, 1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user.getPhoneNumber()).isEqualTo(courier.getPhoneNumber());
        assertThat(result.getBody()).isEqualTo(courier);
        verify(courierService, times(1)).saveAndFlush(any(Courier.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void editCustomer_ValidInput_ReturnsOkResponse() {
        Customer customer = new Customer();
        customer.setPhoneNumber("+79999999111");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        doNothing().when(customerService).saveAndFlush(any(Customer.class));
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<?> result = userService.editCustomer(customer, 1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(user.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
        assertThat(result.getBody()).isEqualTo(customer);
        verify(customerService, times(1)).saveAndFlush(any(Customer.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void blockUser_ValidUserId_ReturnsOkResponse() {
        user.setBlock(true);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        ResponseEntity<?> result = userService.blockUser(user.getId());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertFalse(user.getBlock());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void unblockUser_ValidUserId_ReturnsOkResponse() {
        user.setBlock(false);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        ResponseEntity<?> result = userService.unblockUser(user.getId());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(user.getBlock());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void findByPhoneNumber_ValidPhoneNumber_ReturnsOptionalUser() {
        when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByPhoneNumber("+79999999999");

        assertThat(result.get().getPhoneNumber()).isEqualTo(user.getPhoneNumber());
        verify(userRepository, times(1)).findByPhoneNumber(anyString());
    }

    @Test
    void deleteUser_ValidUserId_ReturnsOkResponse() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(anyInt());

        ResponseEntity<?> result = userService.deleteUser(1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).deleteById(anyInt());
    }
}