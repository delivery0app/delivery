package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.util.common.OrderBPM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

    Courier courier;
    Customer customer;
    Order order;

    @BeforeEach
    void setUp() {
        courier = new Courier();
        courier.setName("Courier_Name");
        courier.setPhoneNumber("+79999999424");
        courier.setEmail("courier@gmail.com");
        courier.setInn("123456789029");

        customer = new Customer();
        customer.setName("Customer_Name");
        customer.setPhoneNumber("+79999999999");
        customer.setEmail("customer@gmail.com");

        order = new Order();
        order.setSenderAddress("Sender Address");
        order.setDeliveryAddress("Delivery Address");
        order.setWeight(5);
        order.setDescription("Description");
        order.setPaymentMethod(OrderBPM.PaymentMethod.CASH);
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setDeliveryDate(LocalDate.now());
        order.setCreationDate(LocalDateTime.now());
        order.setFragileCargo(true);
        order.setPrice(50.0);
        order.setCourier(courier);
        order.setCustomer(customer);

        courierRepository.save(courier);
        customerRepository.save(customer);
        orderRepository.save(order);
    }

    @AfterEach
    void tearDown() {
        courier = null;
        customer = null;
        order = null;

        courierRepository.deleteAll();
        customerRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void findOrdersByCourierId_ValidInput_ReturnsOrdersByCourierId() {
        List<Order> result = orderRepository.findOrdersByCourierId(courier.getId());
        assertThat(result.get(0).getCourier().getEmail()).isEqualTo(courier.getEmail());
    }

    @Test
    void findOrdersByCourierId_NotFound_ReturnsEmptyList() {
        List<Order> result = orderRepository.findOrdersByCourierId(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void findOrdersByCustomerId_ValidInput_ReturnsOrdersByCustomerId() {
        List<Order> result = orderRepository.findOrdersByCustomerId(customer.getId());
        assertThat(result.get(0).getCustomer().getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void findOrdersByCustomerId_NotFound_ReturnsEmptyList() {
        List<Order> result = orderRepository.findOrdersByCustomerId(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void findOrdersByOrderStatus_ValidInput_ReturnsOrdersByStatus() {
        List<Order> result = orderRepository.findOrdersByOrderStatus(order.getOrderStatus());
        assertThat(result.get(0).getCourier().getEmail()).isEqualTo(courier.getEmail());
    }

    @Test
    void findOrdersByOrderStatus_NotFound_ReturnsEmptyList() {
        List<Order> result = orderRepository.findOrdersByOrderStatus(OrderBPM.State.DELIVERED);
        assertTrue(result.isEmpty());
    }
}