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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
@Tag(name = "Admin", description = "Methods for the administrator's work")
public class AdminController {
    private final UserService userService;
    private final CourierService courierService;
    private final CustomerService customerService;
    private final CourierValidator courierValidator;
    private final CustomerValidator customerValidator;
    private final Mapper mapper;

    @Operation(summary = "Blocking a user by id")
    @PostMapping("/users/{user_id}/block")
    public ResponseEntity<?> blockUser(@PathVariable("user_id") int id) {
        return userService.blockUser(id);
    }

    @Operation(summary = "Unlocking a user by id")
    @PostMapping("/users/{user_id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable("user_id") int id) {
        return userService.unblockUser(id);
    }

    @Operation(summary = "Deleting a user by id")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") int userId) {
        return userService.deleteUser(userId);
    }

    @Operation(summary = "Editing a Courier user by ID")
    @PutMapping("/couriers/{user_id}")
    public ResponseEntity<?> editCourier(@RequestBody @Valid CourierDTO courierDTO,
                                         BindingResult bindingResult,
                                         @PathVariable("user_id") int userId) {
        Courier courier = mapper.convertToCourier(courierDTO);
        courier.setId(courierService.findCourierUserId(userId));
        courierValidator.validate(courier, bindingResult);
        ErrorValidation.message(bindingResult);

        return userService.editCourier(courier, userId);
    }

    @Operation(summary = "Editing a Customer user by ID")
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

    @Operation(summary = "Getting information about all Courier users")
    @GetMapping("/couriers")
    public List<CourierDTO> getAllCouriers() {
        return courierService.findAllCouriers().stream()
                .map(mapper::convertToCourierDTO)
                .toList();
    }

    @Operation(summary = "Getting information about all Customer users")
    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.findAllCustomers().stream()
                .map(mapper::convertToCustomerDTO)
                .toList();
    }
}
