package com.factglobal.delivery.models;

import com.factglobal.delivery.util.OrderStatus;
import com.factglobal.delivery.util.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_table")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "sender_address")
    private String senderAddress;
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Column(name = "weight")
    private int weight;
    @Column(name = "description")
    private String description;
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    @Column(name = "distance")
    private int distance;
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "fragile_cargo")
    private boolean fragileCargo;
    @Column(name = "price")
    private double price;
//    @ManyToMany()
//    @JoinColumn(name = "courier_id", referencedColumnName = "id")
//    private Courier courier;
    @ManyToOne()
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
}
