package com.factglobal.delivery.dto.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationAdminDTO {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}
