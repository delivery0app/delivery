package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import com.factglobal.delivery.util.validation.CourierValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;
    private final ModelMapper modelMapper;
    private final CourierValidator courierValidator;
    private final UserService userService;

    @PreAuthorize("hasRole('COURIER')")
    @GetMapping("/info")
    public CourierDTO getCourier(Principal principal) {
        return convertToCourierDTO(courierService.getCourierByPhoneNumber(principal.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<CourierDTO> getAllCourier() {
        return courierService.getAllCourier().stream()
                .map(this::convertToCourierDTO)
                .collect(Collectors.toList());
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{user_id}")
//    public ResponseEntity<?> editCourierByAdmin(@RequestBody @Valid CourierDTO courierDTO,
//                                                  BindingResult bindingResult,
//                                                  @PathVariable("user_id") int id) {
//        Courier courier = convertToCourier(courierDTO);
//        courier.setId(id);
//        courierValidator.validate(courier, bindingResult);
//        ErrorValidation.message(bindingResult);
//
//        return courierService.saveOrUpdate(courier);
//    }
//
//    @PreAuthorize("hasRole('COURIER')")
//    @PutMapping
//    public ResponseEntity<?> editCourier(@RequestBody @Valid CourierDTO courierDTO,
//                                         BindingResult bindingResult,
//                                         Principal principal) {
//        Courier courier = convertToCourier(courierDTO);
//        courier.setId(courierService.getCourierByPhoneNumber(principal.getName()).getId());
//        courierValidator.validate(courier, bindingResult);
//        ErrorValidation.message(bindingResult);
//
//        return courierService.saveOrUpdate(courier);
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteCourierByAdmin(@PathVariable("user_id") int id) {
        return userService.deleteUser(id);
    }

    @PreAuthorize("hasRole('COURIER')")
    @DeleteMapping
    public ResponseEntity<?> deleteCourier(Principal principal) {
        ResponseEntity<?> response = userService.deleteUser(userService.findByPhoneNumber(principal.getName()).orElse(null).getId());
        SecurityContextHolder.clearContext();

        return response;
    }

    private Courier convertToCourier(CourierDTO courierDTO) {
        return modelMapper.map(courierDTO, Courier.class);
    }

    private CourierDTO convertToCourierDTO(Courier courier) {
        return modelMapper.map(courier, CourierDTO.class);
    }
}
