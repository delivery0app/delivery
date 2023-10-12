package com.factglobal.delivery.util.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderBPM {
    public enum State {
        NEW,
        DELIVERED,
        IN_PROGRESS,
        CANCELED
    }

    public enum PaymentMethod {
        CARD,
        CASH
    }
}
