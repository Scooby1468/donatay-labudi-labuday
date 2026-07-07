package com.donation.app.infrastructure.web;

import com.donation.app.domain.DonationException;
import com.donation.app.infrastructure.web.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDonationException_BadRequest() {
        ResponseEntity<ErrorResponse> response = handler.handleDonationException(
                new DonationException("BAD_REQUEST", "Invalid request")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getCode());
        assertEquals("Invalid request", response.getBody().getMessage());
    }

    @Test
    void handleDonationException_Conflict() {
        ResponseEntity<ErrorResponse> response = handler.handleDonationException(
                new DonationException("USER_ALREADY_EXISTS", "User exists")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER_ALREADY_EXISTS", response.getBody().getCode());
    }

    @Test
    void handleDonationException_Unauthorized() {
        ResponseEntity<ErrorResponse> response = handler.handleDonationException(
                new DonationException("INVALID_CREDENTIALS", "Invalid credentials")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getCode());
    }

    @Test
    void handleDonationException_UnknownCode() {
        ResponseEntity<ErrorResponse> response = handler.handleDonationException(
                new DonationException("UNKNOWN", "Unexpected")
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UNKNOWN", response.getBody().getCode());
    }

    @Test
    void handleGeneralException() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(new RuntimeException("Boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getCode());
        assertEquals("Boom", response.getBody().getMessage());
    }
}
