package com.factglobal.delivery.services;


import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.repositories.CourierRepository;
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

    public Courier getCourier(int id) {

        return courierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Courier with id: " + id + " was not found"));
    }

    public void saveCourier(Courier courier) {
        if (courier.getId() == 0)
            enrichCourier(courier);
        courierRepository.save(courier);
    }

    public void deleteCourier(int id) {
        courierRepository.deleteById(id);
    }

    public List<Courier> getAllCourier() {
        List<Courier> orders = courierRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoSuchElementException("No courier has been registered yet");
        }
        return orders;
    }

    private void enrichCourier(Courier courier) {
        courier.setCourierStatus(Courier.CourierStatus.FREE);
    }

    public Courier getCourierByEmail(String email) {

        return courierRepository.findCourierByEmail(email).orElse(null);
    }

    public Courier getCourierByPhoneNumber(String phoneNumber) {
        return courierRepository.findCourierByPhoneNumber(phoneNumber).orElse(null);
    }

    public Courier getCourierByInn(String inn) {
        return courierRepository.findCourierByInn(inn).orElse(null);
    }
}
