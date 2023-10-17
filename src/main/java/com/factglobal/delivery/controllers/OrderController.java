package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.OrderBPM;
import com.factglobal.delivery.util.exception_handling.ErrorMessage;
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
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable("id") int id) {
        return convertToOrderDTO(orderService.getOrder(id));
    }

    @PostMapping("/customers/{id}")
    public ResponseEntity<HttpStatus> createOrderByCustomer(@RequestBody @Valid OrderDTO orderDTO,
                                                            BindingResult bindingResult,
                                                            @PathVariable("id") int id) {
        ErrorMessage.errorMessage(bindingResult);
        orderService.saveOrderByCustomer(convertToOrder(orderDTO), id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") int id) {
        if (orderService.getOrder(id).getOrderStatus() == OrderBPM.State.NEW)
            orderService.deleteOrder(id);
        else
            throw new RuntimeException("This order cannot be delete, it is already in process");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> editOrder(@RequestBody @Valid OrderDTO orderDTO,
                                                BindingResult bindingResult,
                                                @PathVariable("id") int id) {
        Order order = convertToOrder(orderDTO);
        order.setId(id);
        ErrorMessage.errorMessage(bindingResult);
        if (orderDTO.getOrderStatus() == OrderBPM.State.NEW)
            orderService.saveOrder(order);
        else
            throw new RuntimeException("This order cannot be changed, it is already in process");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public List<OrderDTO> getOrdersByCustomer(@PathVariable("id") int id) {
        return orderService.getOrdersByCustomer(id).stream()
                .map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    @GetMapping("/couriers/{id}")
    public List<OrderDTO> getOrdersByCourier(@PathVariable("id") int id) {
        return orderService.getOrdersByCourier(id).stream()
                .map(this::convertToOrderDTO).collect(Collectors.toList());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<HttpStatus> cancelOrder(@PathVariable("id") int id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{order_id}/couriers/{courier_id}")
    public ResponseEntity<HttpStatus> assignCourierForOrder(@PathVariable("order_id") int orderId,
                                                            @PathVariable("courier_id") int courierId) {
        orderService.assignCourierToOrder(orderId, courierId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/couriers")
    public ResponseEntity<HttpStatus> releaseCourierFromOrder(@PathVariable("id") int id) {
        orderService.releaseCourierFromOrder(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Order convertToOrder(OrderDTO orderDTO) {
        return modelMapper.map(orderDTO, Order.class);
    }

    private OrderDTO convertToOrderDTO(Order order) {
        return modelMapper.map(order, OrderDTO.class);
    }
}
