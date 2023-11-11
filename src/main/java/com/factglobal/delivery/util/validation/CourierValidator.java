package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CourierValidator implements Validator {
    private final CourierService courierService;
    @Lazy
    @Autowired
    private UserService userService;

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
            Courier courier = courierService.findCourier(courierNew.getId());
            if (courierService.findCourierByEmail(email) != null && !(email.equals(courier.getEmail()))) {
                errors.rejectValue("email", "", "This email: " + email + " is already taken");
            }
            if (userService.findByPhoneNumber(phoneNumber).isPresent() &&
                    !(phoneNumber.equals(courier.getPhoneNumber()))) {
                errors.rejectValue("phoneNumber", "", "This phone number: " + phoneNumber + " is already taken");
            }
            if (courierService.findCourierByInn(inn) != null && !(inn.equals(courier.getInn()))) {
                errors.rejectValue("inn", "", "This INN number: " + inn + " is already registered");
            }
        }else {
            if (courierService.findCourierByEmail(email) != null) {
                errors.rejectValue("email", "", "This email is already taken");
            }
            if (userService.findByPhoneNumber(phoneNumber).isPresent()) {
                errors.rejectValue("phoneNumber", "", "This phone number is already taken");
            }
            if (courierService.findCourierByInn(inn) != null) {
                errors.rejectValue("inn", "", "This INN number is already registered");
            }
        }

    }
}
