package com.donation.app.infrastructure.persistence;

import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserRepositoryImplTest {

    private SpringDataUserRepository springRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        springRepository = Mockito.mock(SpringDataUserRepository.class);
        userRepository = new UserRepositoryImpl(springRepository);
    }

    @Test
    void findByEmail_Success() {
        UUID id = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
                .id(id)
                .email("test@example.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        when(springRepository.findByEmail("test@example.com")).thenReturn(Mono.just(entity));

        StepVerifier.create(userRepository.findByEmail("test@example.com"))
                .expectNextMatches(user -> user.getId().equals(id) && user.getEmail().equals("test@example.com"))
                .verifyComplete();
    }

    @Test
    void save_Success() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        UserEntity savedEntity = UserEntity.builder()
                .id(id)
                .email("test@example.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .createdAt(user.getCreatedAt())
                .build();

        when(springRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));

        StepVerifier.create(userRepository.save(user))
                .expectNextMatches(savedUser -> savedUser.getId().equals(id))
                .verifyComplete();
    }

    @Test
    void findById_Success() {
        UUID id = UUID.randomUUID();
        UserEntity entity = UserEntity.builder()
                .id(id)
                .email("test@example.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        when(springRepository.findById(id)).thenReturn(Mono.just(entity));

        StepVerifier.create(userRepository.findById(id))
                .expectNextMatches(user -> user.getId().equals(id))
                .verifyComplete();
    }
}
