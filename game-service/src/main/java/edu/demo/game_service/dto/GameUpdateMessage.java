package edu.demo.game_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameUpdateMessage {
    @JsonProperty("tick")
    private int tick;

    @JsonProperty("players")
    private PlayerDamage[] players;

    public GameUpdateMessage() {
    }

    public GameUpdateMessage(int tick, PlayerDamage[] players) {
        this.tick = tick;
        this.players = players;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public PlayerDamage[] getPlayers() {
        return players;
    }

    public void setPlayers(PlayerDamage[] players) {
        this.players = players;
    }

    public static class PlayerDamage {
        @JsonProperty("playerId")
        private String playerId;

        @JsonProperty("damage")
        private int damage;

        public PlayerDamage() {
        }

        public PlayerDamage(String playerId, int damage) {
            this.playerId = playerId;
            this.damage = damage;
        }

        public String getPlayerId() {
            return playerId;
        }

        public void setPlayerId(String playerId) {
            this.playerId = playerId;
        }

        public int getDamage() {
            return damage;
        }

        public void setDamage(int damage) {
            this.damage = damage;
        }
    }
}
