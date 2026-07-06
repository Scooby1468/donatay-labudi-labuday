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
@Schema(description = "Ответ инициализации Google MFA")
public class MfaSetupResponse {
    @Schema(description = "Секретный текстовый ключ")
    private String secret;

    @Schema(description = "Ссылка для создания QR-кода")
    private String qrCodeUrl;
}
