package com.example.matcmaking_api.dto.room;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

public record RoomRequest(
        @NotBlank(message = "Название комнаты не может быть пустым")
        String roomName,

        @NotBlank(message = "Карта не может быть пустой")
        String roomMap,

        @NotNull(message = "ID владельца не может быть пустым")
        Long ownerId,

        @Min(value = 0, message = "Количество игроков не может быть отрицательным")
        int playersCount,

        @Min(value = 1, message = "Максимальное количество игроков должно быть не менее 1")
        int maxPlayers,

        @DecimalMin(value = "0.0", inclusive = true, message = "Средний рейтинг не может быть отрицательным")
        double avgRating,

        @NotBlank(message = "Статус комнаты не может быть пустым")
        String roomStatus,

        @NotNull(message = "Список игроков не может быть null")
        List<PlayerResponse> players,

        @NotNull(message = "Дата создания не может быть пустой")
        OffsetDateTime createdAt
) {
}
