package com.donation.app.usecase;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.domain.UserRepository;
import com.donation.app.infrastructure.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class LoginUserUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private LoginUserUseCase loginUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtProvider = Mockito.mock(JwtProvider.class);
        loginUserUseCase = new LoginUserUseCase(userRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void preLogin_Success() {
        String email = "user@example.com";
        String password = "rawPassword";

        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .role("ROLE_USER")
                .mfaEnabled(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateToken(email, "ROLE_USER")).thenReturn("mockedToken");

        StepVerifier.create(loginUserUseCase.preLogin(email, password))
                .expectNextMatches(result -> !result.mfaRequired() && "mockedToken".equals(result.token()))
                .verifyComplete();
    }

    @Test
    void preLogin_InvalidPassword() {
        String email = "user@example.com";
        String password = "wrongPassword";

        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .role("ROLE_USER")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        StepVerifier.create(loginUserUseCase.preLogin(email, password))
                .expectErrorMatches(throwable -> throwable instanceof DonationException &&
                        ((DonationException) throwable).getCode().equals("INVALID_CREDENTIALS"))
                .verify();
    }

    @Test
    void preLogin_UserNotFound() {
        String email = "unknown@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(loginUserUseCase.preLogin(email, password))
                .expectErrorMatches(throwable -> throwable instanceof DonationException &&
                        ((DonationException) throwable).getCode().equals("INVALID_CREDENTIALS"))
                .verify();
    }

    @Test
    void preLogin_EmptyArgs() {
        StepVerifier.create(loginUserUseCase.preLogin("", "password"))
                .expectErrorMatches(throwable -> throwable instanceof DonationException &&
                        ((DonationException) throwable).getCode().equals("BAD_REQUEST"))
                .verify();
    }
}
