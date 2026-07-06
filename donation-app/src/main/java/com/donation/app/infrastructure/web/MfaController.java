package com.donation.app.infrastructure.web;

import com.donation.app.infrastructure.web.api.MfaApi;
import com.donation.app.infrastructure.web.dto.MfaSetupResponse;
import com.donation.app.infrastructure.web.dto.MfaVerificationRequest;
import com.donation.app.usecase.SendSmsCodeUseCase;
import com.donation.app.usecase.SetupGoogleMfaUseCase;
import com.donation.app.usecase.SetupSmsMfaUseCase;
import com.donation.app.usecase.VerifyAndEnableMfaUseCase;
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
public class MfaController implements MfaApi {

    private final SetupGoogleMfaUseCase setupGoogleMfaUseCase;
    private final SetupSmsMfaUseCase setupSmsMfaUseCase;
    private final SendSmsCodeUseCase sendSmsCodeUseCase;
    private final VerifyAndEnableMfaUseCase verifyAndEnableMfaUseCase;

    @Override
    @PostMapping("/setup-google")
    @Operation(summary = "Шаг 1: Инициализировать подключение Google Authenticator")
    public Mono<MfaSetupResponse> setupGoogleMfa(@AuthenticationPrincipal String email) {
        return setupGoogleMfaUseCase.setupGoogleMfa(email)
                .map(secret -> MfaSetupResponse.builder()
                        .secret(secret)
                        .qrCodeUrl("otpauth://totp/DonationApp:" + email + "?secret=" + secret + "&issuer=DonationApp")
                        .build());
    }

    @Override
    @PostMapping("/setup-sms")
    @Operation(summary = "Шаг 1: Переключить метод 2FA на СМС")
    public Mono<Void> setupSmsMfa(@AuthenticationPrincipal String email) {
        return setupSmsMfaUseCase.enableSmsMfa(email);
    }

    @Override
    @PostMapping("/send-sms-code")
    @Operation(summary = "Шаг 2 (для СМС): Запросить отправку кода подтверждения")
    public Mono<String> sendSmsCode(@AuthenticationPrincipal String email) {
        return sendSmsCodeUseCase.sendSmsCode(email);
    }

    @Override
    @PostMapping("/verify")
    @Operation(summary = "Шаг 3: Верифицировать код и активировать 2FA в профиле")
    public Mono<Boolean> verifyAndEnableMfa(
            @AuthenticationPrincipal String email,
            @RequestBody MfaVerificationRequest request) {
        return verifyAndEnableMfaUseCase.verifyAndEnableMfa(email, request.getCode());
    }
}
