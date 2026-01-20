package com.example.events_contract;

import java.io.Serializable;

public record MatchFoundEvent(
        String matchId,
        String player1Id,
        String player1Nickname,
        int player1Rating,
        String player2Id,
        String player2Nickname,
        int player2Rating,
        String region
) implements Serializable {}
