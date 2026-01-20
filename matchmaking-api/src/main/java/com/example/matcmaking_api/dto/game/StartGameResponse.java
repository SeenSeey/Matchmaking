package com.example.matcmaking_api.dto.game;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "games", itemRelation = "startGame")
public class StartGameResponse extends RepresentationModel<StartGameResponse> {
    private final String gameKey;

    public StartGameResponse(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getGameKey() {
        return gameKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StartGameResponse that = (StartGameResponse) o;
        return Objects.equals(gameKey, that.gameKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameKey);
    }
}
