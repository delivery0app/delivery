package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {
    Optional<Courier> findCourierByPhoneNumber(String phoneNumber);

    Optional<Courier> findCourierByEmail(String email);

    Optional<Courier> findCourierByInn(String inn);
    Optional<Courier> findCourierByUserId(int id);
}
