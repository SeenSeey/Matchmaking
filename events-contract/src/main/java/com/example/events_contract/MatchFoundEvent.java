package com.example.events_contract;

import java.io.Serializable;

public record MatchFoundEvent(
        String matchId,
        String player1Id,
        String player2Id,
        String region
) implements Serializable {}
