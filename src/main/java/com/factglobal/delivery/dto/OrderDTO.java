package com.factglobal.delivery.dto;

import com.factglobal.delivery.util.common.OrderBPM;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Информация о заказе")
public class OrderDTO {
    @Schema(description = "Адрес отправителя")
    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    private String senderAddress;

    @Schema(description = "Адрес доставки")
    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    private String deliveryAddress;

    @Schema(description = "Вес заказа")
    @NotNull(message = "Weight should not be empty")
    @Min(value = 0, message = "Weight should be greater than 0")
    private int weight;

    @Schema(description = "Описание к заказу (по желанию)")
    private String description;

    @Schema(description = "Метод оплаты заказа")
    @NotNull(message = "Payment method should not be empty")
    @Enumerated(EnumType.STRING)
    private OrderBPM.PaymentMethod paymentMethod;

    @Schema(description = "Статус заказа")
    @Enumerated(EnumType.STRING)
    private OrderBPM.State orderStatus;

    @Schema(description = "Предполагаемая дата доставки заказа")
    private LocalDate deliveryDate;

    @Schema(description = "Время создания заказа")
    private LocalDateTime creationDate;

    @Schema(description = "Опция 'хрупкий груз'")
    @NotNull(message = "Fragile Cargo should not be empty")
    private Boolean fragileCargo;

    @Schema(description = "Цена доставки заказа")
    private double price;

}
