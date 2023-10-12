package com.factglobal.delivery.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courier")
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "INN should not be empty")
    @Length(min = 12, max = 12, message = "INN should have 12 characters.")
    @Column(name = "inn")
    private String inn;

    @NotBlank(message = "Phone number should not be empty")
    @Pattern(regexp = "^\\+7\\d{3}\\d{7}$"
            , message = "Phone number must consist of 14 digits and match the format +7XXXХХХХХХХ")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Email
    @NotEmpty
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{8,}"
            , message = "Password should contain at least one number, one lowercase and one uppercase letter, and be at least 8 characters long")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "courier_status")
    private CourierStatus courierStatus;

    @OneToMany(mappedBy = "courier")
    private List<Order> orders;

    public enum CourierStatus {
        FREE, BUSY
    }
}




