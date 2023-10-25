package com.factglobal.delivery.dto.security;

import lombok.Data;

@Data
public class RegistrationCourierDto {
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String inn;
    private String password;
    private String confirmPassword;
}
