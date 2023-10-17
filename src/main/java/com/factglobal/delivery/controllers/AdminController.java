package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.OrderBPM;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

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
    public List<OrderDTO> getOrdersByOrderStatus(@RequestParam(value = "status",
            required = false) String status) {
        OrderBPM.State orderStatus = OrderBPM.State.valueOf(status.toUpperCase());
        return orderService.getOrdersByStatus(orderStatus)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    private OrderDTO convertToDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
