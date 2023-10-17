package com.factglobal.delivery.util.exception_handling;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessage {
    public static void errorMessage(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = new ArrayList<>(bindingResult.getFieldErrors());

            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new EntityNotFoundException(errorMessage.toString());
        }
    }
}
