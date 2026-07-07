package com.donation.app.infrastructure.web.internal;

import com.donation.app.domain.DonationException;
import com.donation.app.usecase.CreateUserProfileUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/users")
public class InternalUserProfileController {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private final CreateUserProfileUseCase createUserProfileUseCase;
    private final String internalToken;

    public InternalUserProfileController(
            CreateUserProfileUseCase createUserProfileUseCase,
            @Value("${app.internal.service-token}") String internalToken) {
        this.createUserProfileUseCase = createUserProfileUseCase;
        this.internalToken = internalToken;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createProfile(
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String token,
            @Valid @RequestBody CreateUserProfileRequest request) {
        if (!internalToken.equals(token)) {
            return Mono.error(new DonationException("INTERNAL_AUTH_FAILED", "Invalid internal service token"));
        }
        return createUserProfileUseCase.createProfile(request.uuid(), request.email()).then();
    }
}
