package edu.demo.game_gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerInfo {
    @JsonProperty("playerId")
    private String playerId;
    
    @JsonProperty("nickname")
    private String nickname;
    
    @JsonProperty("rating")
    private int rating;
    
    @JsonProperty("region")
    private String region;

    public PlayerInfo() {
    }

    public PlayerInfo(String playerId, String nickname, int rating, String region) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.rating = rating;
        this.region = region;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
