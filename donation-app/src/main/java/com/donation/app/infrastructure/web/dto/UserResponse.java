package com.donation.app.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными зарегистрированного пользователя")
public class UserResponse {
    @Schema(description = "Уникальный идентификатор пользователя")
    private UUID id;

    @Schema(description = "Электронная почта")
    private String email;

    @Schema(description = "Роль в системе")
    private String role;
}
