package com.factglobal.delivery.repositories;

import com.factglobal.delivery.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhoneNumber(String username);
}
