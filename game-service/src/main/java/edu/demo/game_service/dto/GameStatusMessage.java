package edu.demo.game_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameStatusMessage {
    @JsonProperty("type")
    private GameStatus type;

    public GameStatusMessage() {
    }

    public GameStatusMessage(GameStatus type) {
        this.type = type;
    }

    public GameStatus getType() {
        return type;
    }

    public void setType(GameStatus type) {
        this.type = type;
    }
}
