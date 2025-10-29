package com.example.matcmaking_api.dto.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateRoomRequest(
        @NotBlank(message = "Название комнаты не может быть пустым")
        String roomName,

        @NotBlank(message = "Карта комнаты не может быть пустой")
        String roomMap,

        @Min(value = 1, message = "Максимальное количество игроков должно быть не менее 1")
        int maxPlayers
) {
}

