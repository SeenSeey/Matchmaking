package com.example.events_contract;

import java.io.Serializable;

public record PlayerSearchingOpponentEvent(
        String playerId,
        String nickname,
        int rating,
        String region
) implements Serializable {}
