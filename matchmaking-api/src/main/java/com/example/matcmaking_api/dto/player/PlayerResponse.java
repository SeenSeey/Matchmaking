package com.example.matcmaking_api.dto.player;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "players", itemRelation = "player")
public class PlayerResponse extends RepresentationModel<PlayerResponse> {
    private final Long playerId;
    private final String username;
    private final int rating;
    private final String region;

    public PlayerResponse(Long playerId, String username, int rating, String region) {
        this.playerId = playerId;
        this.username = username;
        this.rating = rating;
        this.region = region;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlayerResponse that = (PlayerResponse) o;
        return rating == that.rating && Objects.equals(playerId, that.playerId) && Objects.equals(username, that.username) && Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, username, rating, region);
    }
}
