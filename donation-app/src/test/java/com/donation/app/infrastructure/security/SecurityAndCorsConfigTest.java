package com.donation.app.infrastructure.security;

import com.donation.app.infrastructure.jwt.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = SecurityAndCorsConfigTest.TestEndpoints.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class, SecurityAndCorsConfigTest.TestEndpoints.class})
@TestPropertySource(properties = "app.cors.allowed-origins=http://allowed.test")
class SecurityAndCorsConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    void publicAuthEndpointWithoutToken_IsAllowed() {
        webTestClient.get()
                .uri("/api/auth/ping")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("public");
    }

    @Test
    void protectedEndpointWithoutToken_IsUnauthorized() {
        webTestClient.get()
                .uri("/api/profile/ping")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void corsPreflightFromAllowedOrigin_IsAllowed() {
        webTestClient.options()
                .uri("http://localhost/api/auth/ping")
                .header(HttpHeaders.ORIGIN, "http://allowed.test")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://allowed.test")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Test
    void corsPreflightFromUnknownOrigin_IsRejected() {
        webTestClient.options()
                .uri("http://localhost/api/auth/ping")
                .header(HttpHeaders.ORIGIN, "http://evil.test")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .exchange()
                .expectStatus().isForbidden();
    }

    @RestController
    static class TestEndpoints {

        @GetMapping(value = "/api/auth/ping", produces = MediaType.TEXT_PLAIN_VALUE)
        Mono<String> publicPing() {
            return Mono.just("public");
        }

        @GetMapping(value = "/api/profile/ping", produces = MediaType.TEXT_PLAIN_VALUE)
        Mono<String> protectedPing() {
            return Mono.just("protected");
        }
    }
}
