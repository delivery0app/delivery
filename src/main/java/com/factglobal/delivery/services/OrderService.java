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

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
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
        if (order.getId() == 0)
            enrichOrder(order);
        orderRepository.save(order);
    }

    public void saveOrderByCustomer(Order order, int customerId) {
        order.setCustomer(customerRepository.findById(customerId).orElse(null));
    }

    public void cancelOrder(int id) {
        Order order = getOrder(id);
        if (order.getOrderStatus() == OrderBPM.State.NEW)
            order.setOrderStatus(OrderBPM.State.CANCELED);
        else
            throw new RuntimeException();//TODO (This order is already in progress or delivered)
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

    public void assignCourierToOrder(int orderId, int courierId) {
        Order order = getOrder(orderId);
        Courier courier = courierRepository.findById(courierId).orElse(null);

        if (courier != null && courier.getCourierStatus() == Courier.CourierStatus.FREE) {
            order.setCourier(courier);
            courier.setCourierStatus(Courier.CourierStatus.BUSY);
            saveOrder(order);
            courierRepository.save(courier);
        }else
            throw new RuntimeException();//TODO (This courier is already busy or this courier is not exist)

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

    public Double calculateShippingCost(Order order) {
        double price = 0;
        int weight = order.getWeight();
        double coefficientWeigth = 1;

        if (weight > 10) {
            coefficientWeigth = 2;
        } else if (weight <= 10 && weight >=5) {
            coefficientWeigth = 1.5;
        } else if (weight < 5 && weight > 2) {
            coefficientWeigth = 1.3;
        }

        if (order.getFragileCargo())
            price = order.getDistance() * 0.6 * 1.3 * coefficientWeigth;
        else
            price = order.getDistance() * 0.6 * coefficientWeigth;
        return price;
    }

    private void enrichOrder(Order order) {
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setPrice(calculateShippingCost(order));
        order.setCreationDate(LocalDateTime.now());
    }
}
