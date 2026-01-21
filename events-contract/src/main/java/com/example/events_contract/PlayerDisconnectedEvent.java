package com.example.events_contract;

import java.io.Serializable;

public record PlayerDisconnectedEvent(
        String matchId,
        String disconnectedPlayerId
) implements Serializable {}
