package com.donation.app.infrastructure.web.internal;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.usecase.CreateUserProfileUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

class InternalUserProfileControllerTest {

    private CreateUserProfileUseCase useCase;
    private InternalUserProfileController controller;

    @BeforeEach
    void setUp() {
        useCase = Mockito.mock(CreateUserProfileUseCase.class);
        controller = new InternalUserProfileController(useCase, "secret-token");
    }

    @Test
    void createProfile_WithValidToken_Success() {
        UUID uuid = UUID.randomUUID();
        when(useCase.createProfile(uuid, "user@example.com")).thenReturn(Mono.just(User.builder().uuid(uuid).email("user@example.com").build()));

        StepVerifier.create(controller.createProfile("secret-token", new CreateUserProfileRequest(uuid, "user@example.com")))
                .verifyComplete();
    }

    @Test
    void createProfile_WithInvalidToken_ReturnsError() {
        UUID uuid = UUID.randomUUID();

        StepVerifier.create(controller.createProfile("bad-token", new CreateUserProfileRequest(uuid, "user@example.com")))
                .expectErrorMatches(error -> error instanceof DonationException && "INTERNAL_AUTH_FAILED".equals(((DonationException) error).getCode()))
                .verify();
    }
}
