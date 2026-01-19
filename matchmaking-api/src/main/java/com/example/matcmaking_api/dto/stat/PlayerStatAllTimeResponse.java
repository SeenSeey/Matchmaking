package com.example.matcmaking_api.dto.stat;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "stats", itemRelation = "statAllTime")
public class PlayerStatAllTimeResponse extends RepresentationModel<PlayerStatAllTimeResponse> {
    private final PlayerResponse player;
    private final int winsAmount;
    private final int defeatsAmount;

    public PlayerStatAllTimeResponse(PlayerResponse player, int winsAmount, int defeatsAmount) {
        this.player = player;
        this.winsAmount = winsAmount;
        this.defeatsAmount = defeatsAmount;
    }

    public PlayerResponse getPlayer() {
        return player;
    }

    public int getWinsAmount() {
        return winsAmount;
    }

    public int getDefeatsAmount() {
        return defeatsAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerStatAllTimeResponse that = (PlayerStatAllTimeResponse) o;
        return winsAmount == that.winsAmount && defeatsAmount == that.defeatsAmount && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, winsAmount, defeatsAmount);
    }
}
