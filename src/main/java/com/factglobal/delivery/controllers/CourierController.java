package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.util.exception_handling.ErrorMessage;
import com.factglobal.delivery.util.validation.CourierValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/{id}")
    public CourierDTO getCourier(@PathVariable("id") int id) {
        return convertToCourierDTO(courierService.getCourier(id));
    }

    @GetMapping()
    public List<CourierDTO> getAllCourier() {
        return courierService.getAllCourier().stream()
                .map(this::convertToCourierDTO).collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> addCourier(@RequestBody @Valid CourierDTO courierDTO,
                                                 BindingResult bindingResult) {
        Courier courier = convertToCourier(courierDTO);
        courierValidator.validate(courier, bindingResult);
        ErrorMessage.validationError(bindingResult);
        courierService.saveCourier(courier);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> editCourier(@RequestBody @Valid CourierDTO courierDTO,
                                                  BindingResult bindingResult,
                                                  @PathVariable("id") int id) {
        Courier courier = convertToCourier(courierDTO);
        courierValidator.validate(courier, bindingResult);
        ErrorMessage.validationError(bindingResult);
        courier.setId(id);
        courierService.saveCourier(courier);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCourier(@PathVariable("id") int id) {
        courierService.deleteCourier(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Courier convertToCourier(CourierDTO courierDTO) {
        return modelMapper.map(courierDTO, Courier.class);
    }

    private CourierDTO convertToCourierDTO(Courier courier) {
        return modelMapper.map(courier, CourierDTO.class);
    }
}
