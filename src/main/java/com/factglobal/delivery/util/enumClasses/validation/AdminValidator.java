package com.factglobal.delivery.util.enumClasses.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AdminValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
