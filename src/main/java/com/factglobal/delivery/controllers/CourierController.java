package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.validation.CourierValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{user_id}")
    public CourierDTO getCourier(@PathVariable("user_id") int userId) {
        return convertToCourierDTO(courierService.getCourier(userId));
    }

    @GetMapping()
    public List<CourierDTO> getAllCourier() {
        return courierService.getAllCourier().stream()
                .map(this::convertToCourierDTO).collect(Collectors.toList());
    }


    @PutMapping("/{user_id}")
    public ResponseEntity<HttpStatus> editCourier(@RequestBody
//                                                      @Valid
                                                      RegistrationCourierDto registrationCourierDto,
//                                                  BindingResult bindingResult,
                                                  @PathVariable("user_id") int id) {
//        Courier courier = convertToCourier(courierDTO);
//        courierValidator.validate(courier, bindingResult);
//        ErrorMessage.validationError(bindingResult);
        userService.createNewCourier(registrationCourierDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<HttpStatus> deleteCourier(@PathVariable("user_id") int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

//    private Courier convertToCourier(CourierDTO courierDTO) {
//        return modelMapper.map(courierDTO, Courier.class);
//    }

    private CourierDTO convertToCourierDTO(Courier courier) {
        return modelMapper.map(courier, CourierDTO.class);
    }
}
