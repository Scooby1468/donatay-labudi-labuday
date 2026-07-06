package com.donation.app.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос подтверждения MFA")
public class MfaVerificationRequest {
    @Schema(description = "Электронная почта пользователя")
    private String email;

    @Schema(description = "6-значный проверочный код")
    private String code;
}
