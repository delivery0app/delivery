package com.factglobal.delivery.controllers;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/couriers")
public class CourierController {
    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping("/{id}")
    public Courier getCourier(@PathVariable("id") int id) {
        return courierService.getCourier(id);
    }

    @GetMapping()
    public List<Courier> getAllCourier() {
        return courierService.getAllCourier();
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> addCourier(@RequestBody Courier courier) {
        courierService.saveCourier(courier);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<HttpStatus> editCourier(@RequestBody Courier courier) {
        courierService.saveCourier(courier);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCourier(@PathVariable("id") int id) {
        courierService.deleteCourier(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
