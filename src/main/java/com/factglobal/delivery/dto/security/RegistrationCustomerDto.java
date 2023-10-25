package com.factglobal.delivery.dto.security;

import lombok.Data;

@Data
public class RegistrationCustomerDto {
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
}
