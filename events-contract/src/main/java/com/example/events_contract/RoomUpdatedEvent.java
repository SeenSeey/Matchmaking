package com.example.events_contract;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record RoomUpdatedEvent(
        Long roomId,
        String name,
        String roomMap,
        int maxPlayers,
        String status,
        OffsetDateTime updatedAt
) implements Serializable {
}
