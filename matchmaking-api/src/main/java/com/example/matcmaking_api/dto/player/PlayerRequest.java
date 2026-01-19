package com.example.matcmaking_api.dto.player;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PlayerRequest(
        @NotBlank(message = "Никнейм не может быть пустым")
        String nickname,

        @NotBlank(message = "Регион не может быть пустым")
        String region,

        @Min(value = 0, message = "Рейтинг не может быть отрицательным")
        @Max(value = 1000, message = "Рейтинг не может превышать 1000")
        int rating
) {
}
