package com.example.matcmaking_api.dto.room;

import jakarta.validation.constraints.NotNull;

public record JoinRoomRequest(
        @NotNull(message = "ID пользователя не может быть пустым")
        Long userId
) {
}