package edu.demo.matchmaking_gateway.model;

public class Player {
    private String id;
    private String nickname;
    private String region;
    private int rating;

    public Player(String id, String nickname, String region, int rating) {
        this.id = id;
        this.nickname = nickname;
        this.region = region;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
