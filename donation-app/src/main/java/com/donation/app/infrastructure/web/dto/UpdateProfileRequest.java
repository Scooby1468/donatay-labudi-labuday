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
@Schema(description = "Запрос на обновление профиля")
public class UpdateProfileRequest {
    @Schema(description = "Никнейм пользователя", example = "Donator99")
    private String nickname;

    @Schema(description = "Ссылка на аватар", example = "http://example.com/avatar.png")
    private String avatarUrl;

    @Schema(description = "Ссылка на баннер/фон шапки", example = "http://example.com/header.jpg")
    private String headerUrl;

    @Schema(description = "Новая почта", example = "new@example.com")
    private String email;

    @Schema(description = "Новый пароль (минимум 6 символов)", example = "newPassword123")
    private String password;
    
    @Schema(description = "Номер телефона для SMS-MFA", example = "+79998887766")
    private String phoneNumber;
}
