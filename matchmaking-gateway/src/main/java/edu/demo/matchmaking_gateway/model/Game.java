package edu.demo.matchmaking_gateway.model;

import java.sql.Timestamp;

public class Game {
    private Long id;
    private String firstPlayerId;
    private String secondPlayerId;
    private String roomMap;
    private Timestamp gameStartedTime;

    public Game(Long id, String firstPlayerId, String secondPlayerId, String roomMap, Timestamp gameStartedTime) {
        this.id = id;
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.roomMap = roomMap;
        this.gameStartedTime = gameStartedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstPlayerId() {
        return firstPlayerId;
    }

    public void setFirstPlayerId(String firstPlayerId) {
        this.firstPlayerId = firstPlayerId;
    }

    public String getSecondPlayerId() {
        return secondPlayerId;
    }

    public void setSecondPlayerId(String secondPlayerId) {
        this.secondPlayerId = secondPlayerId;
    }

    public String getRoomMap() {
        return roomMap;
    }

    public void setRoomMap(String roomMap) {
        this.roomMap = roomMap;
    }

    public Timestamp getGameStartedTime() {
        return gameStartedTime;
    }

    public void setGameStartedTime(Timestamp gameStartedTime) {
        this.gameStartedTime = gameStartedTime;
    }
}
