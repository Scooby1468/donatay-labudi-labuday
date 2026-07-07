package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProfileUseCasesTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
    }

    @Test
    void getProfile_ByUuidSuccess() {
        UUID uuid = UUID.randomUUID();
        User user = User.builder().uuid(uuid).email("user@example.com").build();
        when(userRepository.findByUuid(uuid)).thenReturn(Mono.just(user));

        StepVerifier.create(new GetProfileUseCase(userRepository).getProfile(uuid.toString()))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void getProfile_ByEmailFallbackSuccess() {
        User user = User.builder().email("user@example.com").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));

        StepVerifier.create(new GetProfileUseCase(userRepository).getProfile("user@example.com"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void getProfile_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(new GetProfileUseCase(userRepository).getProfile("missing@example.com"))
                .expectErrorMatches(error -> error instanceof DonationException && "USER_NOT_FOUND".equals(((DonationException) error).getCode()))
                .verify();
    }

    @Test
    void updateProfile_UpdatesOnlyProfileFields() {
        UUID uuid = UUID.randomUUID();
        User user = User.builder().uuid(uuid).email("user@example.com").password("old-hash").build();
        when(userRepository.findByUuid(uuid)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new UpdateProfileUseCase(userRepository)
                        .updateProfile(uuid.toString(), "Nick", "avatar", "header", "+79990000000"))
                .expectNextMatches(updated -> "Nick".equals(updated.getNickname())
                        && "avatar".equals(updated.getAvatarUrl())
                        && "header".equals(updated.getHeaderUrl())
                        && "+79990000000".equals(updated.getPhoneNumber())
                        && "old-hash".equals(updated.getPassword()))
                .verifyComplete();
    }

    @Test
    void updateProfile_KeepsExistingValuesWhenFieldsAreNull() {
        User user = User.builder().email("user@example.com").nickname("Old").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(new UpdateProfileUseCase(userRepository)
                        .updateProfile("user@example.com", null, null, null, null))
                .expectNextMatches(updated -> "Old".equals(updated.getNickname()))
                .verifyComplete();
    }

    @Test
    void updateProfile_UserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(new UpdateProfileUseCase(userRepository)
                        .updateProfile("missing@example.com", null, null, null, null))
                .expectErrorMatches(error -> error instanceof DonationException && "USER_NOT_FOUND".equals(((DonationException) error).getCode()))
                .verify();
    }
}
