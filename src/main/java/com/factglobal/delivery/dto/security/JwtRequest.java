package com.factglobal.delivery.dto.security;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
