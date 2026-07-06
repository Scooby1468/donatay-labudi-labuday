package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SetupSmsMfaUseCase {
    private final UserRepository userRepository;

    public Mono<Void> enableSmsMfa(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
                        return Mono.error(new DonationException("BAD_REQUEST", "Please add a phone number to your profile first"));
                    }
                    user.setMfaType("SMS");
                    user.setMfaEnabled(false);
                    return userRepository.save(user).then();
                });
    }
}
