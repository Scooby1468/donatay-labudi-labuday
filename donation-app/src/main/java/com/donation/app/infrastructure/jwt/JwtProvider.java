package com.donation.app.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationInSeconds;
    private final String issuer;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:900}") long expirationInSeconds,
            @Value("${jwt.issuer:donatay-auth-service}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationInSeconds = expirationInSeconds;
        this.issuer = issuer;
    }

    public String generateToken(String subject, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInSeconds * 1000);

        return Jwts.builder()
                .claims(Map.of("role", role))
                .issuer(issuer)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
