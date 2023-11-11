package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.dto.CustomerDTO;
import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.OrderBPM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        return userService.editCourier(courierDTO, userId, bindingResult);
    }

    @PutMapping("/customers/{user_id}")
    public ResponseEntity<?> editCustomer(@RequestBody @Valid CustomerDTO customerDTO,
                                         BindingResult bindingResult,
                                         @PathVariable("user_id") int userId) {
        return userService.editCustomer(customerDTO, userId, bindingResult);
    }

    @GetMapping("/couriers")
    public List<CourierDTO> getAllCourier() {
        return courierService.findAllCourier();
    }

    @GetMapping("/customers")
    public List<CustomerDTO> getAllCustomer() {
        return customerService.findAllCustomers();
    }
}
