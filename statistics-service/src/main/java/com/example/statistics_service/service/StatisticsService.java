package com.example.statistics_service.service;

import org.springframework.stereotype.Service;

import com.example.events_contract.RoomCreatedEvent;
import com.example.events_contract.RoomUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticsService {
    private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);

    private final Map<Long, Object> stats = new ConcurrentHashMap<>();

    public void update(RoomCreatedEvent event) {
        stats.put(event.roomId(), event);
        printStats();
    }

    public void update(RoomUpdatedEvent event) {
        stats.put(event.roomId(), event);
        printStats();
    }

    private void printStats() {
        log.info("Statistics: {} rooms tracked", stats.size());
    }
}
