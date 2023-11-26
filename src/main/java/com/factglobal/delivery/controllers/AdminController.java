package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CourierValidator;
import com.factglobal.delivery.util.validation.CustomerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {
    private final UserService userService;
    private final CourierService courierService;
    private final CustomerService customerService;
    private final CourierValidator courierValidator;
    private final CustomerValidator customerValidator;
    private final Mapper mapper;

    @PostMapping("/users/{user_id}/block")
    public ResponseEntity<?> blockUser(@PathVariable("user_id") int id) {
        return userService.blockUser(id);
    }

    @PostMapping("/users/{user_id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable("user_id") int id) {
        return userService.unblockUser(id);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") int userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping("/couriers/{user_id}")
    public ResponseEntity<?> editCourier(@RequestBody @Valid CourierDTO courierDTO,
                                         BindingResult bindingResult,
                                         @PathVariable("user_id") int userId) {
        Courier courier = mapper.convertToCourier(courierDTO);
        courier.setId(courierService.findCourierIdByUserId(userId));
        courierValidator.validate(courier, bindingResult);
        ErrorValidation.message(bindingResult);

        return userService.editCourier(courier, userId);
    }

    @PutMapping("/customers/{user_id}")
    public ResponseEntity<?> editCustomer(@RequestBody @Valid CustomerDTO customerDTO,
                                          BindingResult bindingResult,
                                          @PathVariable("user_id") int userId) {
        Customer customer = mapper.convertToCustomer(customerDTO);
        customer.setId(customerService.findCustomerByUserId(userId));
        customerValidator.validate(customer, bindingResult);
        ErrorValidation.message(bindingResult);

        return userService.editCustomer(customer, userId);
    }

    @GetMapping("/couriers")
    public List<CourierDTO> getAllCourier() {
        return courierService.findAllCourier().stream()
                .map(mapper::convertToCourierDTO)
                .toList();
    }

    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomer() {
        return customerService.findAllCustomers().stream()
                .map(mapper::convertToCustomerDTO)
                .toList();
    }
}
