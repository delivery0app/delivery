package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.exception_handling.ErrorValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Order", description = "Методы для работы с заказами")
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final CourierService courierService;
    private final Mapper mapper;

    @Operation(summary = "Получение информации о всех заказах для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.findAllOrders().stream()
                .map(mapper::convertToOrderDTO)
                .toList();
    }

    @Operation(summary = "Получение информации о заказе для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{order_id}")
    public OrderDTO getOrder(@PathVariable("order_id") int orderId) {
        Order order = orderService.findOrder(orderId);
        return mapper.convertToOrderDTO(order);
    }

    @Operation(summary = "Создание заказа для текущего пользователя Customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO,
                                                  BindingResult bindingResult,
                                                  Principal principal) {
        ErrorValidation.message(bindingResult);
        int customerId = customerService.findCustomerByPhoneNumber(principal.getName()).getId();

        return orderService.saveOrder(mapper.convertToOrder(orderDTO), customerId);
    }

    @Operation(summary = "Удаление заказа для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{order_id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("order_id") int orderId) {
        return orderService.deleteOrder(orderId);
    }

    @Operation(summary = "Редактирование заказа для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/admin")
    public ResponseEntity<?> editOrderByAdmin(@RequestBody @Valid OrderDTO orderDTO,
                                              BindingResult bindingResult,
                                              @PathVariable("order_id") int orderId) {
        ErrorValidation.message(bindingResult);
        Order newOrder = mapper.convertToOrder(orderDTO);
        newOrder.setId(orderId);

        return orderService.editOrderByAdmin(newOrder, orderId);
    }

    @Operation(summary = "Редактирование заказа для текущего пользователя Customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{order_id}")
    public ResponseEntity<?> editOrderByCustomer(@RequestBody @Valid OrderDTO orderDTO,
                                                 BindingResult bindingResult,
                                                 @PathVariable("order_id") int orderId,
                                                 Principal principal) {
        ErrorValidation.message(bindingResult);

        return orderService.editOrderByCustomer(mapper.convertToOrder(orderDTO), orderId, principal);
    }

    @Operation(summary = "Получение всех заказов для текущего пользователя Customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers")
    public List<OrderDTO> getOrdersByCustomer(Principal principal) {
        int customerId = customerService.findCustomerByPhoneNumber(principal.getName()).getId();

        return orderService.findOrdersByCustomer(customerId)
                .stream()
                .map(mapper::convertToOrderDTO)
                .toList();
    }

    @Operation(summary = "Получение заказов определенного заказчика для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers/{customer_id}")
    public List<OrderDTO> getOrdersByCustomerForAdmin(@PathVariable("customer_id") int customerId) {
        return orderService.findOrdersByCustomer(customerId).stream()
                .map(mapper::convertToOrderDTO)
                .toList();
    }

    @Operation(summary = "Получение всех заказов определенного курьера для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/couriers/{courier_id}")
    public List<OrderDTO> getOrdersByCourierForAdmin(@PathVariable("courier_id") int courierId) {
        return orderService.findOrdersByCourier(courierId).stream()
                .map(mapper::convertToOrderDTO)
                .toList();
    }

    @Operation(summary = "Получение заказов для текущего пользователя Courier")
    @PreAuthorize("hasRole('COURIER')")
    @GetMapping("/couriers")
    public List<OrderDTO> getOrdersByCourier(Principal principal) {
        int courierId = courierService.findCourierByPhoneNumber(principal.getName()).getId();

        return orderService.findOrdersByCourier(courierId).stream()
                .map(mapper::convertToOrderDTO)
                .toList();
    }

    @Operation(summary = "Отмена заказа для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/cancel/admin")
    public ResponseEntity<HttpStatus> cancelOrderByAdmin(@PathVariable("order_id") int orderId) {
        return orderService.cancelOrderByAdmin(orderId);
    }

    @Operation(summary = "Отмена заказа для текущего пользователя Customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{order_id}/cancel")
    public ResponseEntity<?> cancelOrderByCustomer(@PathVariable("order_id") int orderId,
                                                   Principal principal) {
        return orderService.cancelOrderByCustomer(orderId, principal);
    }

    @Operation(summary = "Установка статуса DELIVERED заказу для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/delivered/admin")
    public ResponseEntity<HttpStatus> deliveredOrderByAdmin(@PathVariable("order_id") int orderId) {
        return orderService.deliveredOrder(orderId);
    }

    @Operation(summary = "Установка статуса DELIVERED заказу для текущего пользователя Courier")
    @PreAuthorize("hasRole('COURIER')")
    @PutMapping("/{order_id}/delivered")
    public ResponseEntity<?> deliveredOrderByCourier(@PathVariable("order_id") int orderId,
                                                     Principal principal) {
        return orderService.deliveredOrder(orderId, principal);
    }

    @Operation(summary = "Назначение курьера на заказ для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/couriers/{courier_id}/assign")
    public ResponseEntity<?> assignCourierForOrderByAdmin(@PathVariable("order_id") int orderId,
                                                          @PathVariable("courier_id") int courierId) {
        return orderService.assignCourierToOrder(orderId, courierId);
    }

    @Operation(summary = "Назначение курьера на заказ для текущего пользователя Courier")
    @PreAuthorize("hasRole('COURIER')")
    @PutMapping("/{order_id}/couriers/assign")
    public ResponseEntity<?> assignCourierForOrderByCourier(@PathVariable("order_id") int orderId,
                                                            Principal principal) {
        return orderService.assignCourierToOrder(orderId, courierService.findCourierByPhoneNumber(principal.getName()).getId());
    }

    @Operation(summary = "Снятие курьера с заказа для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{order_id}/couriers/{courier_id}/release")
    public ResponseEntity<?> releaseCourierFromOrder(@PathVariable("order_id") int orderId,
                                                     @PathVariable("courier_id") int courierId) {

        return orderService.releaseCourierFromOrder(orderId, courierId);
    }

    @Operation(summary = "Получение заказов по статусу выполнения для Admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public List<OrderDTO> getOrdersByOrderStatus(@RequestParam(value = "status",
            required = false) String status) {
        return orderService.findOrdersByStatus(status).stream()
                .map(mapper::convertToOrderDTO)
                .toList();
    }
}
