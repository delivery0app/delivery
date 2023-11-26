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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CourierService courierService;
    private final CustomerService customerService;
    private final DistanceCalculator distanceCalculator;

    public ResponseEntity<HttpStatus> saveOrder(Order order, int customerId) {

        order.setCustomer(customerService.findCustomer(customerId));
        enrichOrderFromNew(order);
        orderRepository.save(order);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public ResponseEntity<?> editOrderByAdmin(Order newOrder, int orderId) {
        Order oldOrder = findOrder(orderId);
        enrichOrderFromEdit(newOrder, oldOrder);
        orderRepository.save(newOrder);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public ResponseEntity<?> editOrderByCustomer(Order newOrder, int orderId, Principal principal) {
        Order oldOrder = findOrder(orderId);

        Customer customer = customerService.findCustomerByPhoneNumber(principal.getName());
        List<Order> orders = orderRepository.findOrdersByCustomerId(customer.getId());

        if (orders.isEmpty())
            return new ResponseEntity<>("This customer:" + customer.getName() + " does not have this order", HttpStatus.BAD_REQUEST);

        for (Order order : orders) {
            if (order.getId() == orderId) {

                if (order.getOrderStatus() != OrderBPM.State.NEW)
                    throw new IllegalStateException("This order cannot be changed, it is already in process");

                newOrder.setId(orderId);
                enrichOrderFromEdit(newOrder, oldOrder);
                orderRepository.save(newOrder);
            }
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> cancelOrderByAdmin(int orderId) {
        Order order = findOrder(orderId);
        order.setOrderStatus(OrderBPM.State.CANCELED);
        orderRepository.save(order);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public ResponseEntity<?> cancelOrderByCustomer(int orderId, Principal principal) {
        Customer customer = customerService.findCustomerByPhoneNumber(principal.getName());
        List<Order> orders = orderRepository.findOrdersByCustomerId(customer.getId());
        findOrder(orderId);

        for (Order order : orders) {
            if (order.getId() == orderId) {

                if (order.getOrderStatus() == OrderBPM.State.NEW)
                    order.setOrderStatus(OrderBPM.State.CANCELED);
                else
                    throw new IllegalStateException("The order status is: " + order.getOrderStatus() + " and cannot be canceled");

                orderRepository.save(order);

                return ResponseEntity.ok(HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("This customer:" + customer.getName() + " does not have this order", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<HttpStatus> deliveredOrder(int orderId) {
        Order order = findOrder(orderId);
        order.setOrderStatus(OrderBPM.State.DELIVERED);
        orderRepository.save(order);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    public ResponseEntity<?> deliveredOrder(int orderId, Principal principal) {
        Courier courier = courierService.findCourierByPhoneNumber(principal.getName());
        List<Order> orders = orderRepository.findOrdersByCourierId(courier.getId());

        for (Order order : orders) {
            if (order.getId() == orderId) {
                if (order.getOrderStatus() == OrderBPM.State.IN_PROGRESS)
                    order.setOrderStatus(OrderBPM.State.DELIVERED);
                else
                    throw new IllegalStateException("The order status is: " + order.getOrderStatus() + ", but should be in progress");

                orderRepository.save(order);

                return ResponseEntity.ok(HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("This courier:" + courier.getName() + " does not have this order", HttpStatus.BAD_REQUEST);
    }


    public Order findOrder(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order with this id: " + orderId + " does not exist"));
    }

    public List<Order> findAllOrders() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            throw new NoSuchElementException("No orders have been created yet");
        }

        return orders;
    }

    public ResponseEntity<?> deleteOrder(int id) {
        Order order = findOrder(id);

        if (order.getOrderStatus() == OrderBPM.State.DELIVERED
                || order.getOrderStatus() == OrderBPM.State.IN_PROGRESS)
            throw new IllegalStateException("This order cannot be delete, it is already in process");

        orderRepository.delete(order);

        return ResponseEntity.ok("Order with id:" + id + " is delete");
    }

    public ResponseEntity<?> assignCourierToOrder(int orderId, int courierId) {
        Order order = findOrder(orderId);
        Courier courier = courierService.findCourier(courierId);

        if (courier.getCourierStatus() == Courier.Status.FREE &&
                order.getOrderStatus() == OrderBPM.State.NEW) {

            order.setCourier(courier);
            courier.setCourierStatus(Courier.Status.BUSY);
            order.setOrderStatus(OrderBPM.State.IN_PROGRESS);
            orderRepository.save(order);
            courierService.saveAndFlush(courier);
        } else
            throw new IllegalStateException("This courier is already busy or the order is unavailable");

        return ResponseEntity.ok("This courier:" + courier.getName() + "assigned to order" + orderId);
    }

    public ResponseEntity<?> releaseCourierFromOrder(int orderId, int courierId) {
        Order order = findOrder(orderId);
        Courier courier = courierService.findCourier(courierId);

        if (order.getOrderStatus() == OrderBPM.State.IN_PROGRESS &&
                courier.getCourierStatus() == Courier.Status.BUSY) {

            order.setCourier(null);
            order.setOrderStatus(OrderBPM.State.NEW);
            courier.setCourierStatus(Courier.Status.FREE);
            orderRepository.save(order);
            courierService.saveAndFlush(courier);
        } else
            throw new IllegalStateException("this courier does not have an order or " +
                    "the order is either new or completed");

        return ResponseEntity.ok("This courier:" + courier.getName() + " is release");
    }

    public List<Order> findOrdersByCourier(int courierId) {
        List<Order> orders = orderRepository.findOrdersByCourierId(courierId);

        if (orders.isEmpty()) {
            throw new NoSuchElementException("This courier has no orders");
        }

        return orders;
    }

    public List<Order> findOrdersByCustomer(int customerId) {
        List<Order> orders = orderRepository.findOrdersByCustomerId(customerId);

        if (orders.isEmpty())
            throw new NoSuchElementException("This customer has no orders");

        return orders;
    }

    public List<Order> findOrdersByStatus(String status) {
        OrderBPM.State orderStatus = OrderBPM.State.valueOf(status.toUpperCase());

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
        } else if (weight >= 5) {
            coefficientWeigth = 1.5;
        } else if (weight > 2) {
            coefficientWeigth = 1.3;
        }

        if (order.getFragileCargo())
            price = order.getDistance() * 0.01 * 1.3 * coefficientWeigth;
        else
            price = order.getDistance() * 0.01 * coefficientWeigth;

        return price;
    }

    private void enrichOrderFromNew(Order order) {
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

    private void enrichOrderFromEdit(Order orderNew, Order orderOld) {
        try {
            orderNew.setDistance(distanceCalculator.getDistance(orderNew.getSenderAddress(), orderNew.getDeliveryAddress()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Enter the correct city");
        }
        orderNew.setOrderStatus(OrderBPM.State.NEW);
        orderNew.setPrice(Math.ceil(calculateShippingCost(orderNew) * Math.pow(10, 2)) / Math.pow(10, 2));
        orderNew.setCreationDate(orderOld.getCreationDate());
        orderNew.setDeliveryDate(orderOld.getDeliveryDate());
        orderNew.setCustomer(orderOld.getCustomer());
    }
}
