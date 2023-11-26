package com.factglobal.delivery.dto.security;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationAdminDTO {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}
