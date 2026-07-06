package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.dto.MfaSetupResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.MfaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/mfa")
@RequiredArgsConstructor
@Tag(name = "MFA / Двухфакторная аутентификация", description = "Настройка и управление Google & SMS MFA")
@SecurityRequirement(name = "BearerAuth")
public class MfaController {

    private final MfaUseCase mfaUseCase;

    @PostMapping("/setup-google")
    @Operation(summary = "Шаг 1: Инициализировать подключение Google Authenticator")
    public Mono<MfaSetupResponse> setupGoogle(@AuthenticationPrincipal String email) {
        return mfaUseCase.setupGoogleMfa(email)
                .map(secret -> MfaSetupResponse.builder()
                        .secret(secret)
                        .qrCodeUrl("otpauth://totp/DonationApp:" + email + "?secret=" + secret + "&issuer=DonationApp")
                        .build());
    }

    @PostMapping("/setup-sms")
    @Operation(summary = "Шаг 1: Переключить метод 2FA на СМС")
    public Mono<Void> setupSms(@AuthenticationPrincipal String email) {
        return mfaUseCase.enableSmsMfa(email);
    }

    @PostMapping("/send-sms-code")
    @Operation(summary = "Шаг 2 (для СМС): Запросить отправку кода подтверждения")
    public Mono<String> sendSmsCode(@AuthenticationPrincipal String email) {
        return mfaUseCase.sendSmsCode(email);
    }

    @PostMapping("/verify")
    @Operation(summary = "Шаг 3: Верифицировать код и активировать 2FA в профиле")
    public Mono<Boolean> verifyMfa(
            @AuthenticationPrincipal String email,
            @RequestBody MfaVerificationRequest request) {
        return mfaUseCase.verifyAndEnableMfa(email, request.getCode());
    }
}
