package com.factglobal.delivery.util.enumClasses;

import jakarta.persistence.AttributeConverter;

public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return orderStatus.toString();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        try {
            return OrderStatus.valueOf(orderStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
