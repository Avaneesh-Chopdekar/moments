package com.moments.backend.services;

public interface JwtService {

    String generateToken(String username, boolean isAccessToken);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}