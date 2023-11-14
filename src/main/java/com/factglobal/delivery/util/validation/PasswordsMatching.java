package com.factglobal.delivery.util.validation;

import com.factglobal.delivery.dto.security.RegistrationAdminDTO;
import com.factglobal.delivery.dto.security.RegistrationCourierDto;
import com.factglobal.delivery.dto.security.RegistrationCustomerDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordsMatching implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof RegistrationCourierDto registrationCourierDto) {

            if (!registrationCourierDto.getPassword().equals(registrationCourierDto.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "", "Password mismatch");
            }
        } else if (target instanceof RegistrationCustomerDto registrationCustomerDto) {

            if (!registrationCustomerDto.getPassword().equals(registrationCustomerDto.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "", "Password mismatch");
            }
        } else if (target instanceof RegistrationAdminDTO registrationAdminDTO) {

            if (!registrationAdminDTO.getPassword().equals(registrationAdminDTO.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "", "Password mismatch");
            }
        }
    }
}
