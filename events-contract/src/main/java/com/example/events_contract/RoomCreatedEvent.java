package com.example.events_contract;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record RoomCreatedEvent(
        Long roomId,
        String name,
        String roomMap,
        int maxPlayers,
        String status,
        OffsetDateTime createdAt
) implements Serializable {
}
