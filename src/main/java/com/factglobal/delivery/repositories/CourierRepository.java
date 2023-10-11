package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {
}
