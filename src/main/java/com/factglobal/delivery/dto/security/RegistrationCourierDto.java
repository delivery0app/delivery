package com.factglobal.delivery.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Необходимые данные для регистрации пользователя Courier")
public class RegistrationCourierDto {
    @Schema(description = "Имя пользователя Courier")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String name;

    @Schema(description = "Электронная почта пользователя Courier")
    @Email
    @NotBlank(message = "Email number should not be empty")
    private String email;

    @Schema(description = "Номер телефона пользователя Courier")
    @NotBlank(message = "Phone number should not be empty")
    @Pattern(regexp = "^\\+7\\d{3}\\d{7}$"
            , message = "Phone number must consist of 14 digits and match the format +7XXXХХХХХХХ")
    private String phoneNumber;

    @Schema(description = "Номер ИНН пользователя Courier")
    @NotBlank(message = "INN should not be empty")
    @Length(min = 12, max = 12, message = "INN should have 12 characters.")
    private String inn;

    @Schema(description = "Пароль для аутентификации пользователя Courier")
    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z!@#$%^&*]{8,}"
            , message = "Password should contain at least one number, one lowercase and one uppercase letter, and be at least 8 characters long")
    private String password;

    @Schema(description = "Подтверждение пароля для аутентификации пользователя Courier")
    @NotBlank
    private String confirmPassword;
}
