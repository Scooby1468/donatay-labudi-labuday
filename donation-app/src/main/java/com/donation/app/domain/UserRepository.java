package com.donation.app.domain;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface UserRepository {
    Mono<User> findByEmail(String email);
    Mono<User> save(User user);
    Mono<User> findById(UUID id);
}
