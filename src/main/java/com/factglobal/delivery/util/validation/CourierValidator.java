package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.repositories.CourierRepository;
import com.factglobal.delivery.services.CourierService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CourierValidator implements Validator {
    private CourierService courierService;
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
            errors.rejectValue("INN", "", "This INN number is already registered");
        }

    }
}
