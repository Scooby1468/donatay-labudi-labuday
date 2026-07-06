package com.donation.app.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию или вход")
public class AuthRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    @Schema(description = "Электронная почта пользователя", example = "user@example.com")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    @Schema(description = "Пароль пользователя", example = "strongPassword123")
    private String password;
}
