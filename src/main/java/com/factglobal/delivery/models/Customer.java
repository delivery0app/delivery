package com.factglobal.delivery.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @NotBlank(message = "Name should not be empty")
//    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    @Column(name = "name")
    private String name;

//    @NotBlank(message = "Phone number should not be empty")
//    @Pattern(regexp = "^\\+7\\d{3}\\d{7}$"
//            , message = "Phone number must consist of 14 digits and match the format +7XXXХХХХХХХ")
    @Column(name = "phone_number")
    private String phoneNumber;

//    @Email(message = "Email should be valid")
//    @NotEmpty(message = "Email should not be empty")
    @Column(name = "email")
    private String email;


    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


}