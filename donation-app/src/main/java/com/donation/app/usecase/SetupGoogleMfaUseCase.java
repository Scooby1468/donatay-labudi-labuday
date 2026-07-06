package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.infrastructure.mfa.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SetupGoogleMfaUseCase {
    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;

    public Mono<String> setupGoogleMfa(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    String secret = googleAuthService.generateSecretKey();
                    user.setMfaSecret(secret);
                    user.setMfaType("GOOGLE");
                    return userRepository.save(user).map(saved -> secret);
                });
    }
}
