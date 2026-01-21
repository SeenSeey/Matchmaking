package com.example.events_contract;

import java.io.Serializable;

public record PlayerStatusUpdateEvent(
        String playerId,
        String status
) implements Serializable {}
