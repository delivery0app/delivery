package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CustomerValidator implements Validator {
    private final CustomerService customerService;
    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer customerNew = (Customer) target;
        String email = customerNew.getEmail();
        String phoneNumber = customerNew.getPhoneNumber();

        if (customerNew.getId() != 0) {
            Customer customer = customerService.findCustomer(customerNew.getId());
            if (customerService.findCustomerByEmail(email) != null && !(email.equals(customer.getEmail()))) {
                errors.rejectValue("email", "", "This email: " + email + " is already registered to a user");
            }
            if (userService.findByPhoneNumber(phoneNumber).isPresent() &&
                    !(phoneNumber.equals(customer.getPhoneNumber()))) {
                errors.rejectValue("phoneNumber", "", "This phone number: " + phoneNumber + " is already registered to a user");
            }
        } else {
            if (customerService.findCustomerByEmail(email) != null) {
                errors.rejectValue("email", "", "This email: " + email + "is already registered to a user");
            }
            if (userService.findByPhoneNumber(phoneNumber).isPresent()) {
                errors.rejectValue("phoneNumber", "", "This phone number: " + phoneNumber + " is already registered to a user");
            }
        }
    }
}
