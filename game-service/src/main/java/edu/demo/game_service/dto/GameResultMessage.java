package edu.demo.game_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameResultMessage {
    @JsonProperty("result")
    private String result;
    
    @JsonProperty("damageDealt")
    private int damageDealt;
    
    @JsonProperty("damageReceived")
    private int damageReceived;

    public GameResultMessage() {
    }

    public GameResultMessage(String result, int damageDealt, int damageReceived) {
        this.result = result;
        this.damageDealt = damageDealt;
        this.damageReceived = damageReceived;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public int getDamageReceived() {
        return damageReceived;
    }

    public void setDamageReceived(int damageReceived) {
        this.damageReceived = damageReceived;
    }
}
