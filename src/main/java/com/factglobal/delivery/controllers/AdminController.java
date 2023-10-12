package com.factglobal.delivery.controllers;

import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.OrderBPM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {
    private final OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/couriers/{id}/ban")
    public ResponseEntity<HttpStatus> banCourier(@PathVariable("id") int id) {
        //TODO
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/customers/{id}/ban")
    public ResponseEntity<HttpStatus> banCustomer(@PathVariable("id") int id) {
        //TODO
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/couriers/{id}/ban")
    public ResponseEntity<HttpStatus> unbanCourier(@PathVariable("id") int id) {
        //TODO
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}/ban")
    public ResponseEntity<HttpStatus> unbanCustomer(@PathVariable("id") int id) {
        //TODO
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/orders")
    public List<Order> getOrdersByOrderStatus(@RequestParam(value = "status",
            required = false) String status) {
        OrderBPM.State orderStatus = OrderBPM.State.valueOf(status.toUpperCase());
        return orderService.getOrdersByStatus(orderStatus);
    }
}
