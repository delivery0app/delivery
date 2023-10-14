package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.util.validation.CourierValidator;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;
    private final ModelMapper modelMapper;
    private final CourierValidator courierValidator;

    @Autowired
    public CourierController(CourierService courierService, ModelMapper modelMapper, CourierValidator courierValidator) {
        this.courierService = courierService;
        this.modelMapper = modelMapper;
        this.courierValidator = courierValidator;
    }

    @GetMapping("/{id}")
    public Courier getCourier(@PathVariable("id") int id) {
        return courierService.getCourier(id);
    }

    @GetMapping()
    public List<CourierDTO> getAllCourier() {
        return courierService.getAllCourier().stream()
                .map(this::convertToCourierDTO).collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> addCourier(@RequestBody @Valid CourierDTO courierDTO,
                                                 BindingResult bindingResult) {

        courierValidator.validate(convertToCourier(courierDTO), bindingResult);
        errorMessage(bindingResult);
        courierService.saveCourier(convertToCourier(courierDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<HttpStatus> editCourier(@RequestBody @Valid CourierDTO courierDTO
            , BindingResult bindingResult) {
        courierValidator.validate(courierDTO, bindingResult);
        errorMessage(bindingResult);
        courierService.saveCourier(convertToCourier(courierDTO));
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

    static void errorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" – ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new EntityExistsException(errorMsg.toString());
        }
    }
}
