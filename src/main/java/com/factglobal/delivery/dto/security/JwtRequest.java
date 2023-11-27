package com.factglobal.delivery.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Необходимые данные для аутентификации пользователя")
public class JwtRequest {
    @Schema(description = "Номер телефона существующего пользователя")
    private String phoneNumber;
    @Schema(description = "Пароль пользователя")
    private String password;
}
