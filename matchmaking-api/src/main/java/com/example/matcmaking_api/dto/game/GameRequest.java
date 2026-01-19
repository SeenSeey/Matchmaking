package com.example.matcmaking_api.dto.game;

import jakarta.validation.constraints.NotNull;

public record GameRequest(
        @NotNull(message = "ID игрока не может быть пустым")
        String playerId
) {
}