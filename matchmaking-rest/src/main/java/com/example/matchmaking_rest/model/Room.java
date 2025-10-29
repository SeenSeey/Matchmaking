package com.example.matchmaking_rest.model;

import java.time.OffsetDateTime;
import java.util.Set;

public class Room {
    private Long id;
    private String name;
    private String roomMap;
    private int maxPlayers;
    private Set<Long> players;
    private String status;
    private OffsetDateTime createdAt;

    public Room(String name, String roomMap, int maxPlayers, String status, Long ownerId) {
        this.name = name;
        this.roomMap = roomMap;
        this.maxPlayers = maxPlayers;
        this.status = status;

        this.id = null;
        this.players = Set.of(ownerId);
        this.createdAt = OffsetDateTime.now();
    }

    public Room(Long id, String name, String roomMap, int maxPlayers, Set<Long> players, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.roomMap = roomMap;
        this.maxPlayers = maxPlayers;
        this.players = players;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoomMap() {
        return roomMap;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Set<Long> getPlayers() {
        return players;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoomMap(String roomMap) {
        this.roomMap = roomMap;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setPlayers(Set<Long> players) {
        this.players = players;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
