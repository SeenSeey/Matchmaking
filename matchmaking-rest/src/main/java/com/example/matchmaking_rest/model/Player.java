package com.example.matchmaking_rest.model;

public class Player {
    private Long id;
    private String username;
    private String region;
    private int rating;

    public Player(Long id, String username, String region, int rating) {
        this.id = id;
        this.username = username;
        this.region = region;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRegion() {
        return region;
    }

    public int getRating() {
        return rating;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
