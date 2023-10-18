package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.repositories.OrderRepository;
import com.factglobal.delivery.util.common.DistanceCalculator;
import com.factglobal.delivery.util.common.OrderBPM;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CourierService courierService;
    private final CustomerService customerService;
    private final DistanceCalculator distanceCalculator;

    public void saveOrder(Order order) {
        if (order.getId() == 0)
            enrichOrder(order);
        orderRepository.save(order);
    }

    public void saveOrderByCustomer(Order order, int customerId) {
        order.setCustomer(customerService.getCustomer(customerId));
        enrichOrder(order);
        orderRepository.save(order);

    }

    public void cancelOrder(int id) {
        Order order = getOrder(id);
        if (order.getOrderStatus() == OrderBPM.State.NEW)
            order.setOrderStatus(OrderBPM.State.CANCELED);
        else
            throw new IllegalStateException("The order status is: " + order.getOrderStatus() + " and cannot be canceled");
        orderRepository.save(order);
    }

    public void deliveredOrder(int id) {
        Order order = getOrder(id);

        if (order.getOrderStatus() == OrderBPM.State.IN_PROGRESS)
            order.setOrderStatus(OrderBPM.State.DELIVERED);
        else
            throw new IllegalStateException("The order status is: " + order.getOrderStatus() + ", but should be in progress");

    }

    public Order getOrder(int id) {
        Optional<Order> foundOrder = orderRepository.findById(id);
        return foundOrder.orElseThrow(() -> new EntityNotFoundException("Order with this id: " + id + " does not exist"));
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoSuchElementException();
        }
        return orders;
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    public void assignCourierToOrder(int orderId, int courierId) {
        Order order = getOrder(orderId);
        Courier courier = courierService.getCourier(courierId);

        if (courier.getCourierStatus() == Courier.CourierStatus.FREE &&
            order.getOrderStatus() == OrderBPM.State.NEW) {

            order.setCourier(courier);
            courier.setCourierStatus(Courier.CourierStatus.BUSY);
            order.setOrderStatus(OrderBPM.State.IN_PROGRESS);
            saveOrder(order);
            courierService.saveCourier(courier);
        } else
            throw new IllegalStateException("This courier is already busy or the order is unavailable");

    }

    public void releaseCourierFromOrder(int orderId, int courierId) {
        Order order = getOrder(orderId);
        Courier courier = courierService.getCourier(courierId);

        if (order.getOrderStatus() == OrderBPM.State.IN_PROGRESS &&
            courier.getCourierStatus() == Courier.CourierStatus.BUSY) {

            order.setCourier(null);
            order.setOrderStatus(OrderBPM.State.NEW);
            courier.setCourierStatus(Courier.CourierStatus.FREE);
            saveOrder(order);
            courierService.saveCourier(courier);
        } else
            throw new IllegalStateException("this courier does not have an order or " +
                    "the order is either new or completed");
    }

    public List<Order> getOrdersByCourier(int courierId) {
        Courier courier = courierService.getCourier(courierId);
        List<Order> orders = orderRepository.findOrdersByCourier(courier);
        if (orders.isEmpty()) {
            throw new NoSuchElementException("This courier has no orders");
        }
        return orders;
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        Customer customer = customerService.getCustomer(customerId);

        List<Order> orders = orderRepository.findOrdersByCustomer(customer);

        if (orders.isEmpty())
            throw new NoSuchElementException("This customer has no orders");
        return orders;
    }

    public List<Order> getOrdersByStatus(OrderBPM.State orderStatus) {

        List<Order> orders = orderRepository.findOrdersByOrderStatus(orderStatus);
        if (orders.isEmpty()) {
            throw new NoSuchElementException("This status has no orders");
        }
        return orders;
    }

    public Double calculateShippingCost(Order order) {
        double price = 0;
        int weight = order.getWeight();
        double coefficientWeigth = 1;

        if (weight > 10) {
            coefficientWeigth = 2;
        } else if (weight <= 10 && weight >= 5) {
            coefficientWeigth = 1.5;
        } else if (weight < 5 && weight > 2) {
            coefficientWeigth = 1.3;
        }

        if (order.getFragileCargo())
            price = order.getDistance() * 0.01 * 1.3 * coefficientWeigth;
        else
            price = order.getDistance() * 0.01 * coefficientWeigth;

        return price;
    }

    private void enrichOrder(Order order) {
        try {
            order.setDistance(distanceCalculator.getDistance(order.getSenderAddress(), order.getDeliveryAddress()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Enter the correct city");
        }
        order.setOrderStatus(OrderBPM.State.NEW);
        order.setPrice(Math.ceil(calculateShippingCost(order) * Math.pow(10, 2)) / Math.pow(10, 2));
        order.setCreationDate(LocalDateTime.now());
        order.setDeliveryDate(LocalDate.now().plusDays(10));
    }
}
