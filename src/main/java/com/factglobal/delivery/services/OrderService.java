package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.repositories.CourierRepository;
import com.factglobal.delivery.repositories.CustomerRepository;
import com.factglobal.delivery.repositories.OrderRepository;
import com.factglobal.delivery.util.common.OrderBPM;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CourierRepository courierRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.customerRepository = customerRepository;
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Order getOrder(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    public void assignCourierToOrder(Order order, int courierId) {
        Courier courier = courierRepository.findById(courierId).orElse(null);
        order.setCourier(courier);
        orderRepository.save(order);
    }

    public void releaseCourierFromOrder(int orderId) {
        Order order = getOrder(orderId);
        order.setCourier(null);
        orderRepository.save(order);
    }

    public List<Order> getOrdersByCourier(int courierId) {
        Courier courier = courierRepository.findById(courierId).orElse(null);
        return orderRepository.getOrdersByCourier(courier);
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        return orderRepository.getOrdersByCustomer(customer);
    }

    public List<Order> getOrdersByStatus(OrderBPM.State orderStatus) {
        return orderRepository.getOrdersByOrderStatus(orderStatus);
    }

    public Double calculateShippingCost(int distance, int weight, Boolean fragileCargo) {
        return null;
    }
}
