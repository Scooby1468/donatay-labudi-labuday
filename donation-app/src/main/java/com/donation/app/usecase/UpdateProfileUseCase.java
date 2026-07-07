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
public class UpdateProfileUseCase {

    private final UserRepository userRepository;

    public Mono<User> updateProfile(String principal, String nickname, String avatarUrl, String headerUrl, String phoneNumber) {
        return findByPrincipal(principal)
                .switchIfEmpty(Mono.error(new DonationException("USER_NOT_FOUND", "User not found")))
                .flatMap(user -> {
                    if (nickname != null) user.setNickname(nickname);
                    if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
                    if (headerUrl != null) user.setHeaderUrl(headerUrl);
                    if (phoneNumber != null) user.setPhoneNumber(phoneNumber);

                    return userRepository.save(user);
                });
    }

    private Mono<User> findByPrincipal(String principal) {
        try {
            return userRepository.findByUuid(UUID.fromString(principal));
        } catch (RuntimeException ignored) {
            return userRepository.findByEmail(principal);
        }
    }
}
