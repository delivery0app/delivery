package com.factglobal.delivery.dto;

import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.util.common.OrderBPM;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    private String senderAddress;

    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    private String deliveryAddress;

    @NotNull(message = "Weight should not be empty")
    @Min(value = 0, message = "Weight should be greater than 0")
    private int weight;

    private String description;

    @NotNull(message = "Payment method should not be empty")
    @Enumerated(EnumType.STRING)
    private OrderBPM.PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderBPM.State orderStatus;

    private LocalDate deliveryDate;

    private LocalDateTime creationDate;

    @NotNull(message = "Fragile Cargo should not be empty")
    private Boolean fragileCargo;

    private double price;
//
//    private int courierId;
//
//    private int customerId;
}
