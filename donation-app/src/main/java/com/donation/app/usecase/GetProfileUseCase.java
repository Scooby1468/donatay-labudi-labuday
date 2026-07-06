package com.donation.app.usecase;

import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.domain.DonationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GetProfileUseCase {
    private final UserRepository userRepository;

    public Mono<User> getProfile(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")));
    }
}
