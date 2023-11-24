package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class AdminValidator implements Validator {
    @Lazy
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegistrationAdminDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegistrationAdminDTO user = (RegistrationAdminDTO) target;
        String phoneNumber = user.getPhoneNumber();

        if (userService.findByPhoneNumber(phoneNumber).isPresent()) {
            errors.rejectValue("phoneNumber", "", "Administrator with phone number: " + phoneNumber + " is already registered to a user");
        }
    }
}
