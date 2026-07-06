package com.donation.app.infrastructure.web;

import com.donation.app.domain.DonationException;
import com.donation.app.domain.User;
import com.donation.app.infrastructure.jwt.JwtProvider;
import com.donation.app.infrastructure.web.dto.AuthRequest;
import com.donation.app.usecase.LoginUserUseCase;
import com.donation.app.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUserUseCase loginUserUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @WithMockUser
    void register_Success() {
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .role("ROLE_USER")
                .build();

        when(registerUserUseCase.register("test@example.com", "password123"))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.role").isEqualTo("ROLE_USER");
    }

    @Test
    @WithMockUser
    void register_ValidationFailure() {
        AuthRequest request = AuthRequest.builder()
                .email("invalid-email")
                .password("short")
                .build();

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @WithMockUser
    void register_UserExists() {
        AuthRequest request = AuthRequest.builder()
                .email("exists@example.com")
                .password("password123")
                .build();

        when(registerUserUseCase.register("exists@example.com", "password123"))
                .thenReturn(Mono.error(new DonationException("USER_ALREADY_EXISTS", "User already exists")));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("USER_ALREADY_EXISTS");
    }

    @Test
    @WithMockUser
    void login_Success() {
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(loginUserUseCase.preLogin("test@example.com", "password123"))
                .thenReturn(Mono.just(new LoginUserUseCase.PreLoginResult(false, null, "mockToken")));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("mockToken");
    }
}
