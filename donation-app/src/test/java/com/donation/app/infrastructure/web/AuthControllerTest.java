package com.donation.app.infrastructure.web;

import com.donation.app.domain.User;
import com.donation.app.infrastructure.jwt.JwtProvider;
import com.donation.app.infrastructure.web.dto.AuthRequest;
import com.donation.app.infrastructure.web.dto.LoginResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.LoginUserUseCase;
import com.donation.app.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import java.util.UUID;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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
        AuthRequest request = new AuthRequest()
                .email("new@example.com")
                .password("password123");
        UUID uuid = UUID.randomUUID();
        User user = User.builder()
                .id(1L)
                .uuid(uuid)
                .email("new@example.com")
                .role("ROLE_USER")
                .build();

        when(registerUserUseCase.register("new@example.com", "password123"))
                .thenReturn(Mono.just(user));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.uuid").isEqualTo(uuid.toString())
                .jsonPath("$.email").isEqualTo("new@example.com")
                .jsonPath("$.role").isEqualTo("ROLE_USER");
    }

    @Test
    @WithMockUser
    void login_Success() {
        AuthRequest request = new AuthRequest()
                .email("test@example.com")
                .password("password123");

        LoginResponse loginResponse = new LoginResponse()
                .mfaRequired(false)
                .token("mockToken");

        when(loginUserUseCase.login("test@example.com", "password123"))
                .thenReturn(Mono.just(loginResponse));

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

    @Test
    @WithMockUser
    void verifyMfaAndLogin_Success() {
        MfaVerificationRequest request = new MfaVerificationRequest()
                .email("test@example.com")
                .code("123456");

        LoginResponse loginResponse = new LoginResponse()
                .mfaRequired(false)
                .token("mfaToken")
                .email("test@example.com");

        when(loginUserUseCase.verifyMfaAndLogin("test@example.com", "123456"))
                .thenReturn(Mono.just(loginResponse));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/auth/login/verify-mfa")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("mfaToken")
                .jsonPath("$.email").isEqualTo("test@example.com");
    }
}
