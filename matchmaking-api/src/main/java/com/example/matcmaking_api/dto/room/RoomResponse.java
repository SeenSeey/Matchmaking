package com.example.matcmaking_api.dto.room;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Relation(collectionRelation = "rooms", itemRelation = "roomDetail")
public class RoomResponse extends RepresentationModel<RoomResponse> {
    private final Long roomId;
    private final String roomName;
    private final String roomMap;
    private final PlayerResponse owner;
    private final int playersCount;
    private final int maxPlayers;
    private final double avgRating;
    private final String roomStatus;
    private final List<PlayerResponse> players;
    private final OffsetDateTime createdAt;

    public RoomResponse(Long roomId, String roomName, String roomMap, PlayerResponse owner, int playersCount, int maxPlayers, double avgRating, String roomStatus, List<PlayerResponse> players, OffsetDateTime createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomMap = roomMap;
        this.owner = owner;
        this.playersCount = playersCount;
        this.maxPlayers = maxPlayers;
        this.avgRating = avgRating;
        this.roomStatus = roomStatus;
        this.players = players;
        this.createdAt = createdAt;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomMap() {
        return roomMap;
    }

    public PlayerResponse getOwner() {
        return owner;
    }

    public List<PlayerResponse> getPlayers() {
        return players;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoomResponse that = (RoomResponse) o;
        return playersCount == that.playersCount && maxPlayers == that.maxPlayers && Double.compare(avgRating, that.avgRating) == 0 && Objects.equals(roomId, that.roomId) && Objects.equals(roomName, that.roomName) && Objects.equals(roomMap, that.roomMap) && Objects.equals(owner, that.owner) && Objects.equals(roomStatus, that.roomStatus) && Objects.equals(players, that.players) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roomId, roomName, roomMap, owner, playersCount, maxPlayers, avgRating, roomStatus, players, createdAt);
    }
}
