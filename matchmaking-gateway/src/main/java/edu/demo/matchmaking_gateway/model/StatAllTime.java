package edu.demo.matchmaking_gateway.model;

public class StatAllTime {
    private Long id;
    private Long playerId;
    private int winsAmount;
    private int defeatsAmount;

    public StatAllTime(Long id, Long playerId, int winsAmount, int defeatsAmount) {
        this.id = id;
        this.playerId = playerId;
        this.winsAmount = winsAmount;
        this.defeatsAmount = defeatsAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getWinsAmount() {
        return winsAmount;
    }

    public void setWinsAmount(int winsAmount) {
        this.winsAmount = winsAmount;
    }

    public int getDefeatsAmount() {
        return defeatsAmount;
    }

    public void setDefeatsAmount(int defeatsAmount) {
        this.defeatsAmount = defeatsAmount;
    }
}
