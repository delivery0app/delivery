package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.repositories.CourierRepository;
import com.factglobal.delivery.services.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CourierValidator implements Validator {
    private final CourierService courierService;

    @Autowired
    public CourierValidator(CourierService courierService) {
        this.courierService = courierService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Courier.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Courier courier = (Courier) target;
        String email = courier.getEmail();
        String phoneNumber = courier.getPhoneNumber();
        String inn = courier.getInn();
        if (courierService.getCourierByEmail(email) != null) {
            errors.rejectValue("email", "", "This email is already taken");
        }
        if (courierService.getCourierByPhoneNumber(phoneNumber) != null) {
            errors.rejectValue("phoneNumber", "", "This phone number is already taken");
        }
        if (courierService.getCourierByInn(inn) != null) {
            errors.rejectValue("inn", "", "This INN number is already registered");
        }

    }
}
