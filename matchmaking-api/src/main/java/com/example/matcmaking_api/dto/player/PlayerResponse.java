package com.example.matcmaking_api.dto.player;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "players", itemRelation = "player")
public class PlayerResponse extends RepresentationModel<PlayerResponse> {
    private final Long playerId;
    private final String nickname;
    private final int rating;
    private final String region;

    public PlayerResponse(Long playerId, String nickname, int rating, String region) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.rating = rating;
        this.region = region;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
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
        return rating == that.rating && Objects.equals(playerId, that.playerId) && Objects.equals(nickname, that.nickname) && Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, nickname, rating, region);
    }
}
