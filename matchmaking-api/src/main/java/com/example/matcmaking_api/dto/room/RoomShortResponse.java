package com.example.matcmaking_api.dto.room;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.OffsetDateTime;
import java.util.Objects;

@Relation(collectionRelation = "rooms", itemRelation = "room")
public class RoomShortResponse extends RepresentationModel<RoomShortResponse> {
    private final Long roomId;
    private final String roomName;
    private final String roomMap;
    private final Long ownerId;
    private final int playersCount;
    private final int maxPlayers;
    private final double avgRating;
    private final String roomStatus;
    private final OffsetDateTime createdAt;

    public RoomShortResponse(Long roomId, String roomName, String roomMap, Long ownerId, int playersCount, int maxPlayers, double avgRating, String roomStatus, OffsetDateTime createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomMap = roomMap;
        this.ownerId = ownerId;
        this.playersCount = playersCount;
        this.maxPlayers = maxPlayers;
        this.avgRating = avgRating;
        this.roomStatus = roomStatus;
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

    public Long getOwnerId() {
        return ownerId;
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
        RoomShortResponse that = (RoomShortResponse) o;
        return playersCount == that.playersCount && maxPlayers == that.maxPlayers && Double.compare(avgRating, that.avgRating) == 0 && Objects.equals(roomId, that.roomId) && Objects.equals(roomName, that.roomName) && Objects.equals(roomMap, that.roomMap) && Objects.equals(ownerId, that.ownerId) && Objects.equals(roomStatus, that.roomStatus) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roomId, roomName, roomMap, ownerId, playersCount, maxPlayers, avgRating, roomStatus, createdAt);
    }
}
