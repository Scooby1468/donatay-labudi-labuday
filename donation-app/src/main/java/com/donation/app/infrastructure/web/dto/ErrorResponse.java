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
@Schema(description = "Стандартная структура ошибки")
public class ErrorResponse {
    @Schema(description = "Код ошибки", example = "USER_ALREADY_EXISTS")
    private String code;

    @Schema(description = "Описание ошибки", example = "Пользователь с такой почтой уже существует")
    private String message;
}
