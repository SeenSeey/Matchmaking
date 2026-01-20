package edu.demo.matchmaker_service.dto;

public class PlayerInfo {
    private String playerId;
    private String nickname;
    private int rating;
    private String region;

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
