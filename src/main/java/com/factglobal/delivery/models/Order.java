package com.factglobal.delivery.models;

import com.factglobal.delivery.util.common.OrderBPM;
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
@Entity
@Table(name = "order_table")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    @Column(name = "sender_address")
    private String senderAddress;

    @NotBlank(message = "Address should not be empty")
    @Size(min = 2, max = 100, message = "Address should be between 2 and 100 characters")
    @Column(name = "delivery_address")
    private String deliveryAddress;

    @NotNull(message = "Weight should not be empty")
    @Min(value = 0, message = "Weight should be greater than 0")
    @Column(name = "weight")
    private int weight;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Payment method should not be empty")
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private OrderBPM.PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderBPM.State orderStatus;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Transient
    private int distance;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @NotNull(message = "Fragile Cargo should not be empty")
    @Column(name = "fragile_cargo")
    private Boolean fragileCargo;

    @Column(name = "price")
    private double price;
  
    @ManyToOne()
    @JoinColumn(name = "courier_id", referencedColumnName = "id")
    private Courier courier;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
}
