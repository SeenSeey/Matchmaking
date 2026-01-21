package edu.demo.game_service.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private final String matchId;
    private final String player1Id;
    private final String player2Id;
    private final String region;
    private final LocalDateTime startTime;
    private final AtomicInteger player1Damage;
    private final AtomicInteger player2Damage;
    private volatile boolean isRunning;

    public Game(String matchId, String player1Id, String player2Id, String region) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.region = region;
        this.startTime = LocalDateTime.now();
        this.player1Damage = new AtomicInteger(0);
        this.player2Damage = new AtomicInteger(0);
        this.isRunning = true;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public String getRegion() {
        return region;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getPlayer1Damage() {
        return player1Damage.get();
    }

    public int getPlayer2Damage() {
        return player2Damage.get();
    }

    public void addPlayer1Damage(int damage) {
        player1Damage.addAndGet(damage);
    }

    public void addPlayer2Damage(int damage) {
        player2Damage.addAndGet(damage);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        this.isRunning = false;
    }
}
