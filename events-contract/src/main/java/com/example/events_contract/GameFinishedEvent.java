package com.example.events_contract;

import java.io.Serializable;

public record GameFinishedEvent(
        String matchId,
        String player1Id,
        int player1Damage,
        String player2Id,
        int player2Damage,
        String winnerId,
        String region
) implements Serializable {}
