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
@Schema(description = "Запрос первого шага входа с MFA")
public class LoginRequest {
    @Schema(description = "Электронная почта пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль пользователя", example = "strongPassword123")
    private String password;
}
