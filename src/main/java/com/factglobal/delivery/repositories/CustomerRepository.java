package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findCustomerByPhoneNumber(String phoneNumber);

    Optional<Customer> findCustomerByEmail(String email);

    Optional<Customer> findCustomerByUserId(int id);
}
