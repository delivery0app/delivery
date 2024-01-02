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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        User user = new User();
        user.setId(3);
        user.setPhoneNumber("+79999999902");
        user.setPassword("100100100Gt");
        user.setCustomer(customer);

        customer = new Customer();
        customer.setId(1);
        customer.setName("John");
        customer.setPhoneNumber("+79999999902");
        customer.setEmail("customer@gmail.com");
        customer.setOrders(Collections.singletonList(order));
        customer.setUser(user);

        courier = new Courier();
        courier.setId(1);
        courier.setName("John");
        courier.setInn("123412341234");
        courier.setPhoneNumber("+79999999902");
        courier.setEmail("courier6@gmail.com");
        courier.setCourierStatus(Courier.Status.BUSY);
        courier.setUser(user);
        courier.setOrders(Collections.singletonList(order));

        order = Order.builder()
                .id(1)
                .senderAddress("Moscow")
                .deliveryAddress("Paris")
                .weight(7)
                .paymentMethod(OrderBPM.PaymentMethod.CASH)
                .orderStatus(OrderBPM.State.IN_PROGRESS)
                .distance(632)
                .fragileCargo(false)
                .courier(courier)
                .customer(customer)
                .build();

        when(orderRepository.save(order))
                .thenReturn(order);
        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));
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
        assertThat(response).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editOrderByAdmin_ValidInput_ReturnsResponseEntityOk() {
        HttpStatusCode response = orderService.editOrderByAdmin(order, order.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        assertThat(response).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editOrderByCustomer_ValidInput_ReturnsResponseEntityOk() {
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setCourier(null);

        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());
        when(orderRepository.findOrdersByCustomerId(customer.getId()))
                .thenReturn(Collections.singletonList(order));

        HttpStatusCode response = orderService.editOrderByCustomer(order, order.getId(), principal).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(customerService, times(1)).findCustomerByPhoneNumber(customer.getPhoneNumber());
        verify(orderRepository, times(1)).findOrdersByCustomerId(customer.getId());
        verify(orderRepository, times(1)).save(order);
        assertThat(response).isEqualTo(HttpStatus.OK);
    }

    @Test
    void cancelOrderByAdmin_ValidInput_ReturnsResponseEntityOk() {
        HttpStatusCode response = orderService.cancelOrderByAdmin(order.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderBPM.State.CANCELED);
        assertThat(response).isEqualTo(HttpStatus.OK);
    }

    @Test
    void cancelOrderByCustomer_ValidInput_ReturnsResponseEntityOk() {
        order.setCourier(null);
        order.setOrderStatus(OrderBPM.State.NEW);

        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());
        when(orderRepository.findOrdersByCustomerId(customer.getId()))
                .thenReturn(Collections.singletonList(order));

        HttpStatusCode response = orderService.cancelOrderByCustomer(order.getId(), principal).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(customerService, times(1)).findCustomerByPhoneNumber(customer.getPhoneNumber());
        verify(orderRepository, times(1)).findOrdersByCustomerId(customer.getId());
        verify(orderRepository, times(1)).save(order);
        assertSame(order.getOrderStatus(), OrderBPM.State.CANCELED);
        assertThat(response).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deliveredOrderByAdmin_ValidInput_ReturnsResponseEntityOk() {
        HttpStatusCode response = orderService.deliveredOrder(order.getId()).getStatusCode();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        assertSame(order.getOrderStatus(), OrderBPM.State.DELIVERED);
        assertThat(response).isEqualTo(HttpStatus.OK);
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
        assertThat(response).isEqualTo(HttpStatus.OK);
    }

    @Test
    void findOrder_ValidInput_ReturnsOrder() {
        Order responseOrder = orderService.findOrder(order.getId());

        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(order).isEqualTo(responseOrder);
    }

    @Test
    void findAllOrders_ValidInput_ReturnsAllOrders() {
        when(orderRepository.findAll())
                .thenReturn(Collections.singletonList(order));

        List<Order> responseOrders = orderService.findAllOrders();

        verify(orderRepository, times(1)).findAll();
        assertThat(Collections.singletonList(order)).isEqualTo(responseOrders);
    }

    @Test
    void deleteOrder_ValidInput_ReturnsResponseEntityOk() {
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setCourier(null);

        doNothing().when(orderRepository).delete(order);

        HttpStatusCode response = orderService.deleteOrder(order.getId()).getStatusCode();

        verify(orderRepository, times(1)).delete(order);
        assertThat(response).isEqualTo(HttpStatus.OK);
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
        assertThat(response).isEqualTo(HttpStatus.OK);
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
        assertThat(response).isEqualTo(HttpStatus.OK);
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

        assertThat(9.48).isEqualTo(response);
    }

    @Test
    void ifListIsEmptyThrowsNoSuchElementException() {
        when(orderRepository.findAll())
                .thenReturn(Collections.emptyList());
        when(orderRepository.findOrdersByCourierId(anyInt()))
                .thenReturn(Collections.emptyList());
        when(orderRepository.findOrdersByCustomerId(anyInt()))
                .thenReturn(Collections.emptyList());
        when(orderRepository.findOrdersByOrderStatus(order.getOrderStatus()))
                .thenReturn(Collections.emptyList());

        assertThrows(NoSuchElementException.class, () -> orderService.findAllOrders());
        assertThrows(NoSuchElementException.class, () -> orderService.findOrdersByCourier(courier.getId()));
        assertThrows(NoSuchElementException.class, () -> orderService.findOrdersByCustomer(customer.getId()));
        assertThrows(NoSuchElementException.class, () -> orderService.findOrdersByStatus("new"));
    }

    @Test
    void ifOrderStatusIsInvalidThrowsIllegalStateException() {
        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());
        when(courierService.findCourierByPhoneNumber(anyString()))
                .thenReturn(courier);
        when(courierService.findCourier(courier.getId()))
                .thenReturn(courier);
        when(orderRepository.findOrdersByCourierId(courier.getId()))
                .thenReturn(Collections.singletonList(order));
        when(orderRepository.findOrdersByCustomerId(customer.getId()))
                .thenReturn(Collections.singletonList(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrderByCustomer(order.getId(), principal));
        assertThrows(IllegalStateException.class, () -> orderService.editOrderByCustomer(order, order.getId(), principal));
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrderByCustomer(order.getId(), principal));
        assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(order.getId()));
        assertThrows(IllegalStateException.class, () -> orderService.assignCourierToOrder(order.getId(), courier.getId()));

        order.setOrderStatus(OrderBPM.State.NEW);
        courier.setCourierStatus(Courier.Status.FREE);

        assertThrows(IllegalStateException.class, () -> orderService.deliveredOrder(order.getId(), principal));
        assertThrows(IllegalStateException.class, () -> orderService.releaseCourierFromOrder(order.getId(), courier.getId()));
    }

    @Test
    void ifNoOrderByUserReturnsEntityBadRequest() {
        order.setOrderStatus(OrderBPM.State.NEW);

        when(orderRepository.findOrdersByCustomerId(10))
                .thenReturn(Collections.emptyList());
        when(principal.getName())
                .thenReturn(customer.getPhoneNumber());
        when(courierService.findCourierByPhoneNumber(anyString()))
                .thenReturn(courier);
        when(orderRepository.findOrdersByCourierId(courier.getId()))
                .thenReturn(Collections.emptyList());

        HttpStatusCode responseEdit = orderService.editOrderByCustomer(order, order.getId(), principal).getStatusCode();
        HttpStatusCode responseCancel = orderService.cancelOrderByCustomer(order.getId(), principal).getStatusCode();
        HttpStatusCode responseDelivered = orderService.deliveredOrder(order.getId(), principal).getStatusCode();

        assertThat(responseEdit).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseCancel).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseDelivered).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findOrder_InvalidInput_ThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> orderService.findOrder(10));
    }
}