package com.factglobal.delivery.services;


import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.User;
import com.factglobal.delivery.repositories.CourierRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    @Autowired
    @Lazy
    private UserService userService;

    public Courier getCourier(int id) {

        return courierRepository.findCourierByUserId(id)
                .orElseThrow((() -> new EntityNotFoundException("Customer with id: " + id + " was not found")));
    }

    public void saveCourier(Courier courier) {
        if (courier.getId() == 0)
            enrichCourier(courier);

        courierRepository.save(courier);
    }

    public void deleteCourier(int id) {
        userService.deleteUser(id);
    }

    public List<Courier> getAllCourier() {
        List<Courier> orders = courierRepository.findAll();

        if (orders.isEmpty())
            throw new NoSuchElementException("No courier has been registered yet");
        return orders;
    }

    public void enrichCourier(Courier courier) {
        courier.setCourierStatus(Courier.Status.FREE);
    }

    public Courier getCourierByEmail(String email) {

        return courierRepository.findCourierByEmail(email).orElse(null);
    }

    public Courier getCourierByPhoneNumber(String phoneNumber) {
        return courierRepository.findCourierByPhoneNumber(phoneNumber).orElse(null);
    }

    public void saveAndFlush(Courier courier) {
        courierRepository.saveAndFlush(courier);
    }

    public Courier getCourierByInn(String inn) {
        return courierRepository.findCourierByInn(inn).orElse(null);
    }

    public ResponseEntity<?> saveOrUpdate(Courier courier) {
        return ResponseEntity.ok(courierRepository.save(courier));
    }
}
