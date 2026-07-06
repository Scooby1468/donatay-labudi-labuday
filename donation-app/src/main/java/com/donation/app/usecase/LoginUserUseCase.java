package com.donation.app.usecase;

import com.donation.app.domain.UserRepository;
import com.donation.app.domain.DonationException;
import com.donation.app.infrastructure.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Mono<PreLoginResult> preLogin(String email, String rawPassword) {
        if (email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return Mono.error(new DonationException("BAD_REQUEST", "Email and password cannot be empty"));
        }

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("INVALID_CREDENTIALS", "Invalid email or password")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                        if (user.isMfaEnabled()) {
                            return Mono.just(new PreLoginResult(true, user.getMfaType(), null));
                        } else {
                            String token = jwtProvider.generateToken(user.getEmail(), user.getRole());
                            return Mono.just(new PreLoginResult(false, null, token));
                        }
                    } else {
                        return Mono.error(new DonationException("INVALID_CREDENTIALS", "Invalid email or password"));
                    }
                });
    }

    public Mono<String> verifyMfaAndLogin(String email, String codeStr) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new DonationException("INVALID_CREDENTIALS", "Invalid session")))
                .flatMap(user -> {
                    boolean isValid = false;
                    if ("GOOGLE".equals(user.getMfaType())) {
                        try {
                            // verify google totp code
                            // Simple manual instantiation of service or wiring
                            // For simplicity, we can let user have direct check
                            isValid = new com.donation.app.infrastructure.mfa.GoogleAuthService().authorize(user.getMfaSecret(), Integer.parseInt(codeStr));
                        } catch (Exception e) {
                            isValid = false;
                        }
                    } else if ("SMS".equals(user.getMfaType())) {
                        isValid = codeStr != null && codeStr.equals(user.getMfaSecret());
                    }

                    if (isValid) {
                        String token = jwtProvider.generateToken(user.getEmail(), user.getRole());
                        return Mono.just(token);
                    } else {
                        return Mono.error(new DonationException("INVALID_CREDENTIALS", "Invalid code"));
                    }
                });
    }

    public record PreLoginResult(boolean mfaRequired, String mfaType, String token) {}
}

