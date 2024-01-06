package com.factglobal.delivery.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Required data for user authentication")
public class JwtRequest {
    @Schema(description = "Existing user's phone number")
    private String phoneNumber;
    @Schema(description = "User password")
    private String password;
}
