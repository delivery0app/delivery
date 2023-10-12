package com.factglobal.delivery.util.enumClasses.validation;

import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomerValidator implements Validator {
    private final CustomerService customerService;

    @Autowired
    public CustomerValidator(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer customer = (Customer) target;
        String phoneNumber = customer.getPhoneNumber();
        String name = customer.getName();

        if (customerService.getCustomerByPhoneNumber(phoneNumber) != null) {
            errors.rejectValue("phoneNumber", "", "This phone number is already taken");
        }
        if (customerService.getCustomerByEmail(name) != null) {
            errors.rejectValue("email", "", "This email is already taken");
        }
    }
}
