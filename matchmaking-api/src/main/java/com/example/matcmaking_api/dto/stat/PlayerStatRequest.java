package com.example.matcmaking_api.dto.stat;

import jakarta.validation.constraints.NotNull;

public record PlayerStatRequest(
        @NotNull(message = "ID игрока не может быть пустым")
        String playerId
) {
}
