package com.moments.backend.services.impl;

import com.moments.backend.config.AuthProperties;
import com.moments.backend.services.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final AuthProperties authProperties;

    public String generateToken(String username, boolean isAccessToken) {
        long expiration = isAccessToken ? authProperties.getAccessTokenExpiration() : authProperties.getRefreshTokenExpiration();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(authProperties.getJwtSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(authProperties.getJwtSecret().getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(authProperties.getJwtSecret().getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
