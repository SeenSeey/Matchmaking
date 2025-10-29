package com.example.matchmaking_rest.repository;

import com.example.matchmaking_rest.model.Room;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryRoomRepository {
    private final Map<Long, Room> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Room save(Room r) {
        if (r.getId() == null) r.setId(seq.getAndIncrement());
        storage.put(r.getId(), r);
        return r;
    }

    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Room> findAll() { return new ArrayList<>(storage.values()); }

    public List<Room> search(String name, Integer maxPlayers, String roomMap, Boolean openOnly) {
        return storage.values().stream()
                .filter(r -> name == null || r.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(r -> maxPlayers == null || r.getMaxPlayers() <= maxPlayers)
                .filter(r -> roomMap == null || (r.getRoomMap() != null && r.getRoomMap().equalsIgnoreCase(roomMap)))
                .filter(r -> openOnly == null || !openOnly || "OPEN".equalsIgnoreCase(r.getStatus()))
                .sorted(Comparator.comparing(Room::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}