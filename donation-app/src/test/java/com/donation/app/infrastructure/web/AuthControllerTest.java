package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.dto.AuthRequest;
import com.donation.app.infrastructure.web.dto.LoginResponse;
import com.donation.app.infrastructure.web.dto.UserResponse;
import com.donation.app.usecase.LoginUserUseCase;
import com.donation.app.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

    @Test
    @WithMockUser
    void login_Success() {
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(loginUserUseCase.login("test@example.com", "password123"))
                .thenReturn(Mono.just(LoginResponse.builder().mfaRequired(false).token("mockToken").build()));

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
