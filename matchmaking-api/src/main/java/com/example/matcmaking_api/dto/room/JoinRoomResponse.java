package com.example.matcmaking_api.dto.room;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class JoinRoomResponse extends RepresentationModel<JoinRoomResponse> {
    private final Long roomId;
    private final PlayerResponse player;

    public JoinRoomResponse(Long roomId, PlayerResponse player) {
        this.roomId = roomId;
        this.player = player;
    }

    public Long getRoomId() { return roomId; }

    public PlayerResponse getPlayer() { return player; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JoinRoomResponse that = (JoinRoomResponse) o;
        return Objects.equals(roomId, that.roomId) && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roomId, player);
    }
}