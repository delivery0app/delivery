package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.OrderBPM;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(this::convertToOrderDTO).collect(Collectors.toList());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable("id") int id) {
        return convertToOrderDTO(orderService.getOrder(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PostMapping("/customers/{customers_id}")
    public ResponseEntity<HttpStatus> createOrder(@RequestBody @Valid OrderDTO orderDTO,
                                                            BindingResult bindingResult,
                                                            @PathVariable("customers_id") int customersId) {
        ErrorValidation.message(bindingResult);
        orderService.saveOrder(convertToOrder(orderDTO), customersId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @DeleteMapping("/{order_id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("order_id") int orderId) {
        if (orderService.getOrder(orderId).getOrderStatus() != OrderBPM.State.NEW)
            throw new IllegalStateException("This order cannot be delete, it is already in process");

        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PutMapping("/{order_id}")
    public ResponseEntity<HttpStatus> editOrder(@RequestBody @Valid OrderDTO orderDTO,
                                                BindingResult bindingResult,
                                                @PathVariable("order_id") int orderId) {
        ErrorValidation.message(bindingResult);
        if (orderDTO.getOrderStatus() != OrderBPM.State.NEW)
            throw new IllegalStateException("This order cannot be changed, it is already in process");

        orderService.editOrder(convertToOrder(orderDTO), orderId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @GetMapping("/customers/{customer_id}")
    public List<OrderDTO> getOrdersByCustomer(@PathVariable("customer_id") int customerId) {
        return orderService.getOrdersByCustomer(customerId).stream()
                .map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COURIER')")
    @GetMapping("/couriers/{courier_id}")
    public List<OrderDTO> getOrdersByCourier(@PathVariable("courier_id") int courierId) {
        return orderService.getOrdersByCourier(courierId).stream()
                .map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    @PutMapping("/{order_id}/cancel")
    public ResponseEntity<HttpStatus> cancelOrder(@PathVariable("order_id") int orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/delivered")
    public ResponseEntity<HttpStatus> deliveredOrder(@PathVariable("order_id") int orderId) {
        orderService.deliveredOrder(orderId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COURIER')")
    @PutMapping("/{order_id}/couriers/{courier_id}/assign")
    public ResponseEntity<HttpStatus> assignCourierForOrder(@PathVariable("order_id") int orderId,
                                                            @PathVariable("courier_id") int courierId) {
        orderService.assignCourierToOrder(orderId, courierId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{order_id}/couriers/{courier_id}/release")
    public ResponseEntity<HttpStatus> releaseCourierFromOrder(@PathVariable("order_id") int orderId,
                                                              @PathVariable("courier_id") int courierId) {
        orderService.releaseCourierFromOrder(orderId, courierId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Order convertToOrder(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    private OrderDTO convertToOrderDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
