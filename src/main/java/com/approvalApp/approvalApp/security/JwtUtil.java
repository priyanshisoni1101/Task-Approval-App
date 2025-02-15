package com.approvalApp.approvalApp.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Base64;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret.token}")
    private String secretKey;  // Load from application.properties

    @Value("${jwt.expiration.ms}")
    private long expirationTime;  // Load from application.properties

    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractUserId(String token) {
        JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
        Claims claims = (Claims) parser.parseSignedClaims(token).getPayload();
        return claims.getSubject(); // Extract userId (stored in 'sub')
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parser().verifyWith(getSigningKey()).build();
            parser.parseSignedClaims(token);
            return true;  // Token is valid
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;  // Token is invalid
        }
    }
}
