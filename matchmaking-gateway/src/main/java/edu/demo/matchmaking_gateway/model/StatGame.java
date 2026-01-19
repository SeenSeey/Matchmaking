package edu.demo.matchmaking_gateway.model;

public class StatGame {
    private Long id;
    private Long playerId;
    private Long gameId;
    private boolean isWon;
    private int killsAmount;
    private int deathsAmount;
    private int damageDoneAmount;
    private int damageReceivedAmount;
    private int dominanceIndicator;
    private int playerRatingAfterGame;

    public StatGame(Long id, Long playerId, Long gameId, boolean isWon, int killsAmount, int deathsAmount, int damageDoneAmount, int damageReceivedAmount, int dominanceIndicator, int playerRatingAfterGame) {
        this.id = id;
        this.playerId = playerId;
        this.gameId = gameId;
        this.isWon = isWon;
        this.killsAmount = killsAmount;
        this.deathsAmount = deathsAmount;
        this.damageDoneAmount = damageDoneAmount;
        this.damageReceivedAmount = damageReceivedAmount;
        this.dominanceIndicator = dominanceIndicator;
        this.playerRatingAfterGame = playerRatingAfterGame;
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

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public boolean isWon() {
        return isWon;
    }

    public void setWon(boolean won) {
        isWon = won;
    }

    public int getKillsAmount() {
        return killsAmount;
    }

    public void setKillsAmount(int killsAmount) {
        this.killsAmount = killsAmount;
    }

    public int getDeathsAmount() {
        return deathsAmount;
    }

    public void setDeathsAmount(int deathsAmount) {
        this.deathsAmount = deathsAmount;
    }

    public int getDamageDoneAmount() {
        return damageDoneAmount;
    }

    public void setDamageDoneAmount(int damageDoneAmount) {
        this.damageDoneAmount = damageDoneAmount;
    }

    public int getDamageReceivedAmount() {
        return damageReceivedAmount;
    }

    public void setDamageReceivedAmount(int damageReceivedAmount) {
        this.damageReceivedAmount = damageReceivedAmount;
    }

    public int getDominanceIndicator() {
        return dominanceIndicator;
    }

    public void setDominanceIndicator(int dominanceIndicator) {
        this.dominanceIndicator = dominanceIndicator;
    }

    public int getPlayerRatingAfterGame() {
        return playerRatingAfterGame;
    }

    public void setPlayerRatingAfterGame(int playerRatingAfterGame) {
        this.playerRatingAfterGame = playerRatingAfterGame;
    }
}
