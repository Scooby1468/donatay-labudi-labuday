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
@Schema(description = "Ответ на запрос входа с поддержкой MFA")
public class LoginResponse {
    @Schema(description = "Требуется ли прохождение двухфакторной аутентификации", example = "true")
    private boolean mfaRequired;

    @Schema(description = "Тип MFA, если требуется", example = "GOOGLE")
    private String mfaType;

    @Schema(description = "JWT токен (если mfaRequired = false)", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
