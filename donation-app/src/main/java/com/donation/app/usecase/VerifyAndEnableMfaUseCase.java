package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.UserRepository;
import com.donation.app.infrastructure.mfa.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VerifyAndEnableMfaUseCase {
    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;

    public Mono<Boolean> verifyAndEnableMfa(String email, String codeStr) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    boolean isValid = false;
                    if ("GOOGLE".equals(user.getMfaType())) {
                        try {
                            int code = Integer.parseInt(codeStr);
                            isValid = googleAuthService.authorize(user.getMfaSecret(), code);
                        } catch (NumberFormatException e) {
                            isValid = false;
                        }
                    } else if ("SMS".equals(user.getMfaType())) {
                        isValid = codeStr != null && codeStr.equals(user.getMfaSecret());
                    }

                    if (isValid) {
                        user.setMfaEnabled(true);
                        return userRepository.save(user).map(saved -> true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }
}
