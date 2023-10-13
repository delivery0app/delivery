package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.OrderBPM;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

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
    public ResponseEntity<HttpStatus> createOrderByCustomer(@RequestBody OrderDTO orderDTO,
                                                            @PathVariable("id") int id) {
        orderService.saveOrderByCustomer(convertToOrder(orderDTO), id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") int id) {
        if (orderService.getOrder(id).getOrderStatus() == OrderBPM.State.NEW)
            orderService.deleteOrder(id);
        else
            throw new RuntimeException();//TODO (This order cannot be delete, it is already in process)
        //но мне кажется, что тут не должны эти ограничения стоять, если этот метод попадёт в руки админа, то
        //у него не должно быть ограничений, а вот уже если заказчик будет менять заказ,
        //то там и надо  будет прописывать ограничения.
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<HttpStatus> editOrder(@RequestBody OrderDTO orderDTO) {
        if (orderDTO.getOrderStatus() == OrderBPM.State.NEW)
            orderService.saveOrder(convertToOrder(orderDTO));
        else
            throw new RuntimeException();//TODO (This order cannot be changed, it is already in process)
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

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}/couriers")
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
