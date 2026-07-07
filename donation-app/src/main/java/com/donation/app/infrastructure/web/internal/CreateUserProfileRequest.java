package com.donation.app.infrastructure.web.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUserProfileRequest(
        @NotNull UUID uuid,
        @Email @NotBlank String email
) {
}
