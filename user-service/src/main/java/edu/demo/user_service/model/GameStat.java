package edu.demo.user_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "game_stats")
public class GameStat {
    @Id
    private String id;
    private String matchId;
    private String player1Id;
    private int player1Damage;
    private String player2Id;
    private int player2Damage;
    private String winnerId;
    private String region;
    private LocalDateTime finishedAt;

    public GameStat() {
    }

    public GameStat(String matchId, String player1Id, int player1Damage, 
                    String player2Id, int player2Damage, String winnerId, String region) {
        this.matchId = matchId;
        this.player1Id = player1Id;
        this.player1Damage = player1Damage;
        this.player2Id = player2Id;
        this.player2Damage = player2Damage;
        this.winnerId = winnerId;
        this.region = region;
        this.finishedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer1Damage() {
        return player1Damage;
    }

    public void setPlayer1Damage(int player1Damage) {
        this.player1Damage = player1Damage;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public int getPlayer2Damage() {
        return player2Damage;
    }

    public void setPlayer2Damage(int player2Damage) {
        this.player2Damage = player2Damage;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
