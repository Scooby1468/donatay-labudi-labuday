package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.infrastructure.mfa.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class MfaUseCase {

    private final UserRepository userRepository;
    private final GoogleAuthService googleAuthService;
    private final SecureRandom secureRandom = new SecureRandom();

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

    public Mono<Void> enableSmsMfa(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
                        return Mono.error(new DonationException("BAD_REQUEST", "Please add a phone number to your profile first"));
                    }
                    user.setMfaType("SMS");
                    user.setMfaEnabled(false); // Enable only upon first verification code
                    return userRepository.save(user).then();
                });
    }

    public Mono<String> sendSmsCode(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    if (!"SMS".equals(user.getMfaType())) {
                        return Mono.error(new DonationException("BAD_REQUEST", "SMS MFA is not selected for this user"));
                    }
                    // Generate 6-digit code
                    int code = 100000 + secureRandom.nextInt(900000);
                    String codeStr = String.valueOf(code);
                    user.setMfaSecret(codeStr);
                    
                    return userRepository.save(user).map(saved -> {
                        log.info("🔔 [ИМИТАЦИЯ SMS-ШЛЮЗА] Отправка SMS на номер {} с кодом: {}", user.getPhoneNumber(), codeStr);
                        return codeStr; // Return code for demonstration / client verification in test
                    });
                });
    }

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
