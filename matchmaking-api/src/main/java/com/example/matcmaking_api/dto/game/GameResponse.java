package com.example.matcmaking_api.dto.game;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.sql.Timestamp;
import java.util.Objects;

@Relation(collectionRelation = "games", itemRelation = "game")
public class GameResponse extends RepresentationModel<GameResponse> {
    private final Long gameId;
    private final PlayerResponse firstPlayer;
    private final PlayerResponse secondPlayer;
    private final String roomMap;
    private final Timestamp gameStartedTime;

    public GameResponse(Long gameId, PlayerResponse firstPlayer, PlayerResponse secondPlayer, String roomMap, Timestamp gameStartedTime) {
        this.gameId = gameId;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.roomMap = roomMap;
        this.gameStartedTime = gameStartedTime;
    }

    public Long getGameId() {
        return gameId;
    }

    public PlayerResponse getFirstPlayer() {
        return firstPlayer;
    }

    public PlayerResponse getSecondPlayer() {
        return secondPlayer;
    }

    public String getRoomMap() {
        return roomMap;
    }

    public Timestamp getGameStartedTime() {
        return gameStartedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GameResponse that = (GameResponse) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(firstPlayer, that.firstPlayer) && Objects.equals(secondPlayer, that.secondPlayer) && Objects.equals(roomMap, that.roomMap) && Objects.equals(gameStartedTime, that.gameStartedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gameId, firstPlayer, secondPlayer, roomMap, gameStartedTime);
    }
}
