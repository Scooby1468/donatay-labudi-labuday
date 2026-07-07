package com.donation.app.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderTest {

    private static final String SHARED_SECRET = "test-shared-jwt-secret-at-least-32-characters-long";
    private static final String ISSUER = "donatay-auth-service";

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SHARED_SECRET, 900, ISSUER);
    }

    @Test
    void generateAndValidateToken_Success() {
        String userUuid = UUID.randomUUID().toString();
        String role = "ROLE_USER";

        String token = jwtProvider.generateToken(userUuid, role);
        assertNotNull(token);

        assertTrue(jwtProvider.validateToken(token));

        Claims claims = jwtProvider.getClaims(token);
        assertEquals(userUuid, claims.getSubject());
        assertEquals(ISSUER, claims.getIssuer());
        assertEquals(role, claims.get("role"));
    }

    @Test
    void validateToken_Invalid() {
        assertFalse(jwtProvider.validateToken("invalidTokenStructure"));
    }

    @Test
    void validateToken_RejectsUnexpectedIssuer() {
        String token = jwtProvider.generateToken(UUID.randomUUID().toString(), "ROLE_USER");
        JwtProvider providerWithOtherIssuer = new JwtProvider(SHARED_SECRET, 900, "other-issuer");

        assertFalse(providerWithOtherIssuer.validateToken(token));
    }

    @Test
    void validateToken_RejectsExpiredToken() {
        JwtProvider provider = new JwtProvider(SHARED_SECRET, -1, ISSUER);

        String token = provider.generateToken(UUID.randomUUID().toString(), "ROLE_USER");

        assertFalse(provider.validateToken(token));
    }
}
