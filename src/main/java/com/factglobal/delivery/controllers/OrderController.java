package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.services.UserService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.common.OrderBPM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;
    private final CourierService courierService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.findAllOrders();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{order_id}")
    public OrderDTO getOrderDTO(@PathVariable("order_id") int orderId) {
        return orderService.findOrderDTO(orderId);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<HttpStatus> createOrder(@RequestBody @Valid OrderDTO orderDTO,
                                                            BindingResult bindingResult,
                                                            Principal principal) {
        return orderService.saveOrder(orderDTO, customerService.findCustomerByPhoneNumber(principal.getName()).getId(), bindingResult);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{order_id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("order_id") int orderId) {
        return orderService.deleteOrder(orderId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/admin")
    public ResponseEntity<?> editOrderByAdmin(@RequestBody @Valid OrderDTO orderDTO,
                                                BindingResult bindingResult,
                                                @PathVariable("order_id") int orderId) {
        return orderService.editOrderByAdmin(orderDTO, orderId, bindingResult);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{order_id}")
    public ResponseEntity<?> editOrderByCustomer(@RequestBody @Valid OrderDTO orderDTO,
                                       BindingResult bindingResult,
                                       @PathVariable("order_id") int orderId,
                                       Principal principal) {
        return orderService.editOrderByCustomer(orderDTO, orderId, bindingResult, principal);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customers")
    public List<OrderDTO> getOrdersByCustomer(Principal principal) {
        return orderService.findOrdersByCustomer(customerService.findCustomerByPhoneNumber(principal.getName()).getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customers/{customer_id}")
    public List<OrderDTO> getOrdersByCustomerByAdmin(@PathVariable("customer_id") int customerId) {
        return orderService.findOrdersByCustomer(customerId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/couriers/{courier_id}")
    public List<OrderDTO> getOrdersByCourierByAdmin(@PathVariable("courier_id") int courierId) {
        return orderService.findOrdersByCourier(courierId);
    }

    @PreAuthorize("hasRole('COURIER')")
    @GetMapping("/couriers")
    public List<OrderDTO> getOrdersByCourier(Principal principal) {
        return orderService.findOrdersByCourier(courierService.findCourierByPhoneNumber(principal.getName()).getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/cancel/admin")
    public ResponseEntity<HttpStatus> cancelOrderByAdmin(@PathVariable("order_id") int orderId) {
        return orderService.cancelOrderByAdmin(orderId);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{order_id}/cancel")
    public ResponseEntity<?> cancelOrderByCustomer(@PathVariable("order_id") int orderId,
                                                  Principal principal) {
        return orderService.cancelOrderByCustomer(orderId, principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/delivered/admin")
    public ResponseEntity<HttpStatus> deliveredOrderByAdmin(@PathVariable("order_id") int orderId) {
        return orderService.deliveredOrder(orderId);
    }

    @PreAuthorize("hasRole('COURIER')")
    @PutMapping("/{order_id}/delivered")
    public ResponseEntity<?> deliveredOrderByCourier(@PathVariable("order_id") int orderId,
                                                              Principal principal) {
        return orderService.deliveredOrder(orderId, principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{order_id}/couriers/{courier_id}/assign")
    public ResponseEntity<?> assignCourierForOrderByAdmin(@PathVariable("order_id") int orderId,
                                                            @PathVariable("courier_id") int courierId) {
        return orderService.assignCourierToOrder(orderId, courierId);
    }

    @PreAuthorize("hasRole('COURIER')")
    @PutMapping("/{order_id}/couriers/assign")
    public ResponseEntity<?> assignCourierForOrderByCourier(@PathVariable("order_id") int orderId,
                                                            Principal principal) {
        return orderService.assignCourierToOrder(orderId, courierService.findCourierByPhoneNumber(principal.getName()).getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{order_id}/couriers/{courier_id}/release")
    public ResponseEntity<?> releaseCourierFromOrder(@PathVariable("order_id") int orderId,
                                                              @PathVariable("courier_id") int courierId) {

        return orderService.releaseCourierFromOrder(orderId, courierId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public List<OrderDTO> getOrdersByOrderStatus(@RequestParam(value = "status",
                                                required = false) String status) {
        return orderService.findOrdersByStatus(status);
    }
}
