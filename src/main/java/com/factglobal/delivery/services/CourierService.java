package com.factglobal.delivery.services;


import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.repositories.CourierRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourierService {
    private final CourierRepository courierRepository;

    @Autowired
    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public Courier getCourier(int id) {
        return courierRepository.findById(id).orElse(null);
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
        return courierRepository.findAll();
    }

    private void enrichCourier(Courier courier) {
        courier.setCourierStatus(Courier.CourierStatus.FREE);
    }

}
