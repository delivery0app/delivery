package com.factglobal.delivery.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Required data for registration a Admin user ")
public class RegistrationAdminDTO {
    @Schema(description = "Admin user phone number")
    @NotBlank(message = "Phone number should not be empty")
    @Pattern(regexp = "^\\+7\\d{3}\\d{7}$"
            , message = "Phone number must consist of 14 digits and match the format +7XXXХХХХХХХ")
    private String phoneNumber;

    @Schema(description = "Admin user authentication password")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{8,}"
            , message = "Password should contain at least one number, one lowercase and one uppercase letter, and be at least 8 characters long")
    private String password;

    @Schema(description = "Password confirmation for Admin user authentication")
    @NotBlank
    private String confirmPassword;
}
