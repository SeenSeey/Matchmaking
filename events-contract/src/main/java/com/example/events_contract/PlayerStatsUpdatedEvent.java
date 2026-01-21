package com.example.events_contract;

import java.io.Serializable;

public record PlayerStatsUpdatedEvent(
        String playerId,
        int rating,
        int winsCount,
        int lossesCount
) implements Serializable {}
