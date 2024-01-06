package com.factglobal.delivery.dto;

import com.factglobal.delivery.util.common.OrderBPM;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Information about order")
public class OrderDTO {
    @Schema(description = "Sender's address")
    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    private String senderAddress;

    @Schema(description = "Delivery address")
    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    private String deliveryAddress;

    @Schema(description = "Order weight")
    @NotNull(message = "Weight should not be empty")
    @Min(value = 0, message = "Weight should be greater than 0")
    private int weight;

    @Schema(description = "Description for ordering (optional)")
    private String description;

    @Schema(description = "Order payment method")
    @NotNull(message = "Payment method should not be empty")
    @Enumerated(EnumType.STRING)
    private OrderBPM.PaymentMethod paymentMethod;

    @Schema(description = "Order status")
    @Enumerated(EnumType.STRING)
    private OrderBPM.State orderStatus;

    @Schema(description = "Estimated delivery date of the order")
    private LocalDate deliveryDate;

    @Schema(description = "Order creation time")
    private LocalDateTime creationDate;

    @Schema(description = "Option 'fragile cargo'")
    @NotNull(message = "Fragile Cargo should not be empty")
    private Boolean fragileCargo;

    @Schema(description = "Order delivery price")
    private double price;

}
