package com.moments.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.auth")
public class AuthProperties {
    private String jwtSecret;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
}