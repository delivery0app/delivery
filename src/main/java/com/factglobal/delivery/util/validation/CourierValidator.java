package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.services.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CourierValidator implements Validator {
    private final CourierService courierService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Courier.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Courier courierNew = (Courier) target;
        String email = courierNew.getEmail();
        String phoneNumber = courierNew.getPhoneNumber();
        String inn = courierNew.getInn();

        if (courierNew.getId() != 0) {
            Courier courier = courierService.getCourier(courierNew.getId());
            if (courierService.getCourierByEmail(email) != null && !(email.equals(courier.getEmail()))) {
                errors.rejectValue("email", "", "This email: " + email + " is already taken");
            }
            if (courierService.getCourierByPhoneNumber(phoneNumber) != null &&
                    !(phoneNumber.equals(courier.getPhoneNumber()))) {
                errors.rejectValue("phoneNumber", "", "This phone number: " + phoneNumber + " is already taken");
            }
            if (courierService.getCourierByInn(inn) != null && !(inn.equals(courier.getInn()))) {
                errors.rejectValue("inn", "", "This INN number: " + inn + " is already registered");
            }
        }else {
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
}
