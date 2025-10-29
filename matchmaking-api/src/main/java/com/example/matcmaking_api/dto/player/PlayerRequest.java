package com.example.matcmaking_api.dto.player;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlayerRequest(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String username,

        @NotBlank(message = "Регион не может быть пустым")
        String region,

        @Min(value = 0, message = "Рейтинг не может быть отрицательным")
        int rating
) {
}
