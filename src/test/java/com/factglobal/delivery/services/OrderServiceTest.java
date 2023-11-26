package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.repositories.OrderRepository;
import com.factglobal.delivery.util.common.OrderBPM;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {
    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private CourierService courierService;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @MockBean
    private Principal principal;

    private Order order;
    private Customer customer;
    private Courier courier;

    @BeforeEach
    void setUp() {
        User user = new User(3, "+79999999902", "100100100Gt", customer, false, null, null);
        customer = new Customer(1, "John", "+79999999902", "customer@gmail.com", Collections.singletonList(order), user);
        courier = new Courier(1, "John", "123412341234",
                "+79999999902", "courier6@gmail.com", Courier.Status.BUSY, user, Collections.singletonList(order));
        order = new Order(1, "Moscow", "Paris", 7, null,
                OrderBPM.PaymentMethod.CASH, OrderBPM.State.IN_PROGRESS, null, 632,
                null, false, 0, courier, customer);

        when(orderRepository.save(order))
                .thenReturn(order);
        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));
        when(orderRepository.findOrdersByCustomerId(customer.getId()))
                .thenReturn(Collections.singletonList(order));
        when(customerService.findCustomerByPhoneNumber(anyString()))
                .thenReturn(customer);
    }

    @Test
    void saveOrder_ValidInput_ReturnsResponseEntityOk() {
        when(customerService.findCustomer(customer.getId()))
                .thenReturn(customer);

        HttpStatusCode response = orderService.saveOrder(order, customer.getId()).getStatusCode();

        verify(customerService, times(1)).findCustomer(customer.getId());
        verify(orderRepository, times(1)).save(order);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void editOrderByAdmin_ValidInput_ReturnsResponseEntityOk() {
        HttpStatusCode response = orderService.editOrderByAdmin(order, order.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void editOrderByCustomer_ValidInput_ReturnsResponseEntityOk() {
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setCourier(null);

        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());

        HttpStatusCode response = orderService.editOrderByCustomer(order, order.getId(), principal).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(customerService, times(1)).findCustomerByPhoneNumber(customer.getPhoneNumber());
        verify(orderRepository, times(1)).findOrdersByCustomerId(customer.getId());
        verify(orderRepository, times(1)).save(order);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void editOrderByCustomer_InvalidInput_ThrowsIllegalStateException() {
        order.setOrderStatus(OrderBPM.State.IN_PROGRESS);

        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());

        assertThrows(IllegalStateException.class, () ->orderService.editOrderByCustomer(order, order.getId(), principal));
    }

    @Test
    void cancelOrderByAdmin_ValidInput_ReturnsResponseEntityOk() {
        HttpStatusCode response = orderService.cancelOrderByAdmin(order.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        assertSame(order.getOrderStatus(), OrderBPM.State.CANCELED);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void cancelOrderByCustomer_ValidInput_ReturnsResponseEntityOk() {
        order.setCourier(null);
        order.setOrderStatus(OrderBPM.State.NEW);

        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());

        HttpStatusCode response = orderService.cancelOrderByCustomer(order.getId(), principal).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(customerService, times(1)).findCustomerByPhoneNumber(customer.getPhoneNumber());
        verify(orderRepository, times(1)).findOrdersByCustomerId(customer.getId());
        verify(orderRepository, times(1)).save(order);
        assertSame(order.getOrderStatus(), OrderBPM.State.CANCELED);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void cancelOrderByCustomer_InvalidInput_ThrowsIllegalStateException() {
        order.setOrderStatus(OrderBPM.State.IN_PROGRESS);

        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());

        assertThrows(IllegalStateException.class, () ->orderService.cancelOrderByCustomer(order.getId(), principal));
    }

    @Test
    void deliveredOrderByAdmin_ValidInput_ReturnsResponseEntityOk() {
        HttpStatusCode response = orderService.deliveredOrder(order.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        assertSame(order.getOrderStatus(), OrderBPM.State.DELIVERED);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void deliveredOrderByCourier_ValidInput_ReturnsResponseEntityOk() {
        when(principal.getName())
                .thenReturn(courier.getPhoneNumber());
        when(courierService.findCourierByPhoneNumber(anyString()))
                .thenReturn(courier);
        when(orderRepository.findOrdersByCourierId(courier.getId()))
                .thenReturn(Collections.singletonList(order));

        HttpStatusCode response = orderService.deliveredOrder(order.getId(), principal).getStatusCode();

        verify(courierService, times(1)).findCourierByPhoneNumber(courier.getPhoneNumber());
        verify(orderRepository, times(1)).findOrdersByCourierId(courier.getId());
        verify(orderRepository, times(1)).save(order);
        assertSame(order.getOrderStatus(), OrderBPM.State.DELIVERED);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void findOrder_ValidInput_ReturnsOrder() {
        Order responseOrder = orderService.findOrder(order.getId());

        verify(orderRepository, times(1)).findById(order.getId());
        assertEquals(order, responseOrder);
    }

    @Test
    void findOrder_InvalidInput_ThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> orderService.findOrder(10));
    }

    @Test
    void findAllOrders_ValidInput_ReturnsAllOrders() {
        when(orderRepository.findAll())
                .thenReturn(Collections.singletonList(order));

        List<Order> responseOrders = orderService.findAllOrders();

        verify(orderRepository, times(1)).findAll();
        assertEquals(Collections.singletonList(order), responseOrders);
    }

    @Test
    void findOrder_InvalidInput_ThrowsNoSuchElementException() {
        when(orderRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () ->orderService.findAllOrders());
    }

    @Test
    void deleteOrder_ValidInput_ReturnsResponseEntityOk() {
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setCourier(null);

        doNothing().when(orderRepository).delete(order);

        HttpStatusCode response = orderService.deleteOrder(order.getId()).getStatusCode();

        verify(orderRepository, times(1)).delete(order);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void assignCourierToOrder_ValidInput_ReturnsResponseEntityOk() {
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setCourier(null);
        courier.setCourierStatus(Courier.Status.FREE);
        courier.setOrders(null);

        when(courierService.findCourier(courier.getId()))
                .thenReturn(courier);
        doNothing().when(courierService).saveAndFlush(courier);

        HttpStatusCode response = orderService.assignCourierToOrder(order.getId(), courier.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(courierService, times(1)).findCourier(courier.getId());
        verify(orderRepository, times(1)).save(order);
        verify(courierService, times(1)).saveAndFlush(courier);
        assertSame(order.getOrderStatus(), OrderBPM.State.IN_PROGRESS);
        assertSame(courier.getCourierStatus(), Courier.Status.BUSY);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void releaseCourierFromOrder_ValidInput_ReturnsResponseEntityOk() {
        when(courierService.findCourier(courier.getId()))
                .thenReturn(courier);
        doNothing().when(courierService).saveAndFlush(courier);

        HttpStatusCode response = orderService.releaseCourierFromOrder(order.getId(), courier.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(courierService, times(1)).findCourier(courier.getId());
        verify(orderRepository, times(1)).save(order);
        verify(courierService, times(1)).saveAndFlush(courier);
        assertSame(order.getOrderStatus(), OrderBPM.State.NEW);
        assertSame(courier.getCourierStatus(), Courier.Status.FREE);
        assertEquals(HttpStatus.OK, response);
    }

    @Test
    void findOrdersByCourier_ValidInput_ReturnsOrders() {
        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findOrdersByCourierId(courier.getId()))
                .thenReturn(orders);

        List<Order> responseOrders = orderService.findOrdersByCourier(courier.getId());

        verify(orderRepository, times(1)).findOrdersByCourierId(courier.getId());
        assertSame(orders, responseOrders);
    }

    @Test
    void findOrdersByCustomer_ValidInput_ReturnsOrders() {
        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findOrdersByCustomerId(customer.getId()))
                .thenReturn(orders);

        List<Order> responseOrders = orderService.findOrdersByCustomer(customer.getId());

        verify(orderRepository, times(1)).findOrdersByCustomerId(customer.getId());
        assertSame(orders, responseOrders);
    }

    @Test
    void findOrdersByStatus_ValidInput_ReturnsOrders() {
        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findOrdersByOrderStatus(order.getOrderStatus()))
                .thenReturn(orders);

        List<Order> responseOrders = orderService.findOrdersByStatus("in_progress");

        verify(orderRepository, times(1)).findOrdersByOrderStatus(order.getOrderStatus());
        assertSame(orders, responseOrders);
    }

    @Test
    void calculateShippingCost_ValidInput_ReturnsDouble() {
        Double response = orderService.calculateShippingCost(order);

        assertEquals(9.48, response);
    }
}