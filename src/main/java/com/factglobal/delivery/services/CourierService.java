package com.factglobal.delivery.services;


import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.repositories.CourierRepository;
import com.factglobal.delivery.util.common.Mapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final Mapper mapper;

    public Courier findCourier(int id) {
        return courierRepository.findById(id)
                .orElseThrow((() -> new EntityNotFoundException("Customer with id: " + id + " was not found")));
    }

    public Integer findCourierByUserId(int userId) {
        return courierRepository.findCourierByUserId(userId)
                .orElseThrow((() -> new EntityNotFoundException("Customer with user_id: " + userId + " was not found")))
                .getId();
    }

    public List<Courier> findAllCourier() {
        List<Courier> orders = courierRepository.findAll();

        if (orders.isEmpty())
            throw new NoSuchElementException("No courier has been registered yet");

        return orders;
    }

    public void saveAndFlush(Courier courier) {
        courierRepository.saveAndFlush(courier);
    }

    public Courier findCourierByEmail(String email) {
        return courierRepository.findCourierByEmail(email).orElse(null);
    }


    public Courier findCourierByPhoneNumber(String phoneNumber) {
        return courierRepository.findCourierByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("Courier with id: " + phoneNumber + " was not found"));
    }

    public Courier findCourierByInn(String inn) {
        return courierRepository.findCourierByInn(inn).orElse(null);
    }

    public void enrichCourier(Courier courier) {
        courier.setCourierStatus(Courier.Status.FREE);
    }
}
