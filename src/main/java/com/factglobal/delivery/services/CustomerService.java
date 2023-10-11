package com.factglobal.delivery.services;

import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public Customer getCustomer(int id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);
    }

    public void deleteCustomer(int id) {
        customerRepository.deleteById(id);
    }

    public List<Order> getAllOrders() {
        return null;
    }

    public List<Order> getAllOrdersCompleted() {
        return null;
    }

    public Order getOrder(int customer_id, int order_id) {
        return null;
    }

    public void cancelOrder() {
    }
}
