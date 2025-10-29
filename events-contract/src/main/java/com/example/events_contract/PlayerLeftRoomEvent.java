package com.example.events_contract;

import java.io.Serializable;

public record PlayerLeftRoomEvent(
        Long playerId,
        Long roomId,
        int currentPlayersCount
) implements Serializable {
}
