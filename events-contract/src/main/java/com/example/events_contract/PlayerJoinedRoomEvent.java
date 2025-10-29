package com.example.events_contract;

import java.io.Serializable;

public record PlayerJoinedRoomEvent(
        Long playerId,
        Long roomId,
        String username,
        String region,
        int rating,
        int currentPlayersCount
) implements Serializable {
}
