package com.moments.backend.dto;

public record JwtResponseDto(
        String accessToken,
        String refreshToken
) {
}