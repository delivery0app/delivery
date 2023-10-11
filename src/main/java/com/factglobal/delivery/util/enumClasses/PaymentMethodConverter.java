package com.factglobal.delivery.util.enumClasses;

import jakarta.persistence.AttributeConverter;

public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {
    @Override
    public String convertToDatabaseColumn(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        return paymentMethod.toString();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        try {
            return PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
