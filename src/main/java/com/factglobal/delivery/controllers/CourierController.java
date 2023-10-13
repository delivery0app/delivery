package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.CourierDTO;
import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;
    private final ModelMapper modelMapper;

    @Autowired
    public CourierController(CourierService courierService, ModelMapper modelMapper) {
        this.courierService = courierService;
        this.modelMapper = modelMapper;
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
    public ResponseEntity<HttpStatus> addCourier(@RequestBody CourierDTO courierDTO) {
        courierService.saveCourier(convertToCourier(courierDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<HttpStatus> editCourier(@RequestBody CourierDTO courierDTO) {
        courierService.saveCourier(convertToCourier(courierDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCourier(@PathVariable("id") int id) {
        courierService.deleteCourier(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    private Courier convertToCourier(CourierDTO courierDTO ) {
        return modelMapper.map(courierDTO, Courier.class);
    }
    private CourierDTO convertToCourierDTO(Courier courier) {
        return modelMapper.map(courier, CourierDTO.class);
    }
}
