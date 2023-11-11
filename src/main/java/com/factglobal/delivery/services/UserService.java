package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.repositories.UserRepository;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CourierService courierService;
    private final CustomerService customerService;
    private final RoleService roleService;
    private final Mapper mapper;
    private final CourierValidator courierValidator;
    private final CustomerValidator customerValidator;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public User findById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "User with id: " + userId + " was not found"
        ));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User with phone number:'%s' not found", phoneNumber)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().
                        map(role -> new SimpleGrantedAuthority(role.getName())).
                        collect(Collectors.toList())
        );
    }

    private User createAndSaveUser(String phoneNumber, String password, String role) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(roleService.getUserRole(role)));
        user.setBlock(true);

        return userRepository.saveAndFlush(user);
    }

    public User createNewCourier(RegistrationCourierDto registrationCourierDto) {
        User savedUser = createAndSaveUser(registrationCourierDto.getPhoneNumber(), registrationCourierDto.getPassword(), "ROLE_COURIER");

        Courier courier = mapper.convertToCourier(registrationCourierDto);
        courier.setUser(savedUser);
        courierService.enrichCourier(courier);
        courierService.saveAndFlush(courier);

        savedUser.setCourier(courier);

        return userRepository.save(savedUser);
    }

    public User createNewCustomer(RegistrationCustomerDto registrationCustomerDto) {
        User savedUser = createAndSaveUser(registrationCustomerDto.getPhoneNumber(), registrationCustomerDto.getPassword(), "ROLE_CUSTOMER");

        Customer customer = mapper.convertToCustomer(registrationCustomerDto);
        customer.setUser(savedUser);
        customerService.saveAndFlush(customer);

        savedUser.setCustomer(customer);

        return userRepository.save(savedUser);
    }

    public User createNewAdmin(RegistrationAdminDTO registrationAdminDTO) {
        return createAndSaveUser(registrationAdminDTO.getPhoneNumber(), registrationAdminDTO.getPassword(), "ROLE_ADMIN");
    }

    public ResponseEntity<?> editCourier(CourierDTO courierDTO, int  userId, BindingResult bindingResult) {
        Courier courier = mapper.convertToCourier(courierDTO);
        User user = findById(userId);

        courier.setId(courierService.findCourierByUserId(userId));
        courierValidator.validate(courier, bindingResult);
        ErrorValidation.message(bindingResult);

        courier.setUser(user);
        courierService.saveAndFlush(courier);

        user.setPhoneNumber(courier.getPhoneNumber());
        user.setCourier(courier);
        userRepository.save(user);

        return ResponseEntity.ok(courier);
    }

    public ResponseEntity<?> editCustomer(CustomerDTO customerDTO, int  userId, BindingResult bindingResult) {
        User user = findById(userId);
        Customer customer = mapper.convertToCustomer(customerDTO);

        customer.setId(customerService.findCustomerByUserId(userId));
        customerValidator.validate(customer, bindingResult);
        ErrorValidation.message(bindingResult);

        customer.setUser(user);
        customerService.saveAndFlush(customer);

        user.setPhoneNumber(customer.getPhoneNumber());
        user.setCustomer(customer);
        userRepository.save(user);

        return ResponseEntity.ok(customer);
    }

    public ResponseEntity<?> blockUser(int id) {
        User user = findById(id);
        user.setBlock(false);
        userRepository.saveAndFlush(user);

        return ResponseEntity.ok("This user with id:" + id + " is block");
    }

    public ResponseEntity<?> unblockUser(int id) {
        User user = findById(id);
        user.setBlock(true);
        userRepository.saveAndFlush(user);

        return ResponseEntity.ok("This user with id:" + id + " is unblock");
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public ResponseEntity<?> deleteUser(int id) {
        String phoneNumber = findById(id).getPhoneNumber();
        userRepository.deleteById(id);

        return ResponseEntity.ok().body("User with phone number:" + phoneNumber + " is delete");
    }
}
