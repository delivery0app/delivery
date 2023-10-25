package com.factglobal.delivery.services;

import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.repositories.CourierRepository;
import com.factglobal.delivery.repositories.CustomerRepository;
import com.factglobal.delivery.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final CourierRepository courierRepository;
    private final CustomerRepository customerRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public User findById(int userId) {
        return userRepository.findById(userId).orElseThrow((() -> new EntityNotFoundException("Customer with id: " + userId + " was not found")));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    private User createAndSaveUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(roleService.getUserRole(role)));
        return userRepository.saveAndFlush(user);
    }

    public User createNewCourier(RegistrationCourierDto registrationCourierDto) {
        User savedUser = createAndSaveUser(registrationCourierDto.getUsername(), registrationCourierDto.getPassword(), "ROLE_COURIER");

        Courier courier = new Courier();
        courier.setInn(registrationCourierDto.getInn());
        courier.setPhoneNumber(registrationCourierDto.getPhoneNumber());
        courier.setEmail(registrationCourierDto.getEmail());
        courier.setName(registrationCourierDto.getName());
        courier.setUser(savedUser);
        courier.setCourierStatus(Courier.Status.FREE);

        courierRepository.saveAndFlush(courier);

        savedUser.setCourier(courier);

        return userRepository.save(savedUser);
    }

    public User createNewCustomer(RegistrationCustomerDto registrationCustomerDto) {
        User savedUser = createAndSaveUser(registrationCustomerDto.getUsername(), registrationCustomerDto.getPassword(), "ROLE_CUSTOMER");

        Customer customer = new Customer();
        customer.setPhoneNumber(registrationCustomerDto.getPhoneNumber());
        customer.setEmail(registrationCustomerDto.getEmail());
        customer.setName(registrationCustomerDto.getName());
        customer.setUser(savedUser);

        customerRepository.saveAndFlush(customer);

        savedUser.setCustomer(customer);

        return userRepository.save(savedUser);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}
