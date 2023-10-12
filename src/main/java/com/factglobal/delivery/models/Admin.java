package com.factglobal.delivery.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Name should not be empty")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Name should not be empty")
    @Column(name = "password")
    private String password;

    @OneToMany
    private List<Customer> customers;

    @OneToMany
    private List<Courier> couriers;

    @OneToMany
    private List<Order> orders;
}
