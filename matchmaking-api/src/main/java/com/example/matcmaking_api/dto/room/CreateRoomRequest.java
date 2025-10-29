package com.example.matcmaking_api.dto.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
        @NotBlank(message = "Название комнаты не может быть пустым")
        String roomName,

        @NotBlank(message = "Карта не может быть пустой")
        String roomMap,

        @NotBlank(message = "Статус комнаты не может быть пустым")
        String roomStatus,

        @Min(value = 1, message = "Максимальное количество игроков должно быть не менее 1")
        int maxPlayers,

        @NotNull(message = "ID владельца не может быть пустым")
        Long ownerId
) {
}
