package com.factglobal.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о пользователе Customer")
public class CustomerDTO {
    @Schema(description = "Имя пользователя Customer")
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String name;

    @Schema(description = "Номер телефона пользователя Customer")
    @NotBlank(message = "Phone number should not be empty")
    @Pattern(regexp = "^\\+7\\d{3}\\d{7}$"
            , message = "Phone number should consist of 14 digits and match the format +7XXXХХХХХХХ")
    private String phoneNumber;

    @Schema(description = "Электронная почта пользователя Customer")
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email should not be empty")
    private String email;
}
