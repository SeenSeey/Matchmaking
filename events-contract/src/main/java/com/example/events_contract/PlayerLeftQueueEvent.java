package com.example.events_contract;

import java.io.Serializable;

public record PlayerLeftQueueEvent(
        String playerId,
        String region
) implements Serializable {}
