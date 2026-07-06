package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendSmsCodeUseCase {
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public Mono<String> sendSmsCode(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    if (!"SMS".equals(user.getMfaType())) {
                        return Mono.error(new DonationException("BAD_REQUEST", "SMS MFA is not selected for this user"));
                    }
                    int code = 100000 + secureRandom.nextInt(900000);
                    String codeStr = String.valueOf(code);
                    user.setMfaSecret(codeStr);
                    
                    return userRepository.save(user).map(saved -> {
                        log.info("🔔 [ИМИТАЦИЯ SMS-ШЛЮЗА] Отправка SMS на номер {} с кодом: {}", user.getPhoneNumber(), codeStr);
                        return codeStr;
                    });
                });
    }
}
