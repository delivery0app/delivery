//package com.factglobal.delivery.util.validation;
//
//import com.factglobal.delivery.models.Customer;
//import com.factglobal.delivery.services.CustomerService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//@Component
//@RequiredArgsConstructor
//public class CustomerValidator implements Validator {
//    private final CustomerService customerService;
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return Customer.class.equals(clazz);
//    }
//
//    @Override
//    public void validate(Object target, Errors errors) {
//        Customer customerNew = (Customer) target;
//        String email = customerNew.getEmail();
//        String phoneNumber = customerNew.getPhoneNumber();
//
//        if (customerNew.getCustomerId() != 0) {
//            Customer customer = customerService.getCustomer(customerNew.getCustomerId());
//            if (customerService.getCustomerByEmail(email) != null && !(email.equals(customer.getEmail()))) {
//                errors.rejectValue("email", "", "This email: " + email + " is already taken");
//            }
//            if (customerService.getCustomerByPhoneNumber(phoneNumber) != null &&
//                    !(phoneNumber.equals(customer.getPhoneNumber()))) {
//                errors.rejectValue("phoneNumber", "", "This phone number: " + phoneNumber + " is already taken");
//            }
//        }else {
//            if (customerService.getCustomerByEmail(email) != null) {
//                errors.rejectValue("email", "", "This email: " + email + "is already taken");
//            }
//            if (customerService.getCustomerByPhoneNumber(phoneNumber) != null) {
//                errors.rejectValue("phoneNumber", "", "This phone number: " + phoneNumber + " is already taken");
//            }
//        }
//    }
//}
