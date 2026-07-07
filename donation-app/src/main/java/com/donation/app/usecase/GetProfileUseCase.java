package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProfileUseCase {
    private final UserRepository userRepository;

    public Mono<User> getProfile(String principal) {
        return findByPrincipal(principal)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")));
    }

    private Mono<User> findByPrincipal(String principal) {
        try {
            return userRepository.findByUuid(UUID.fromString(principal));
        } catch (RuntimeException ignored) {
            return userRepository.findByEmail(principal);
        }
    }
}
