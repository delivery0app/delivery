package com.factglobal.delivery.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class JwtRequest {
    private String phoneNumber;
    private String password;
}
