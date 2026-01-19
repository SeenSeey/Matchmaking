package com.example.matcmaking_api.dto.stat;

import com.example.matcmaking_api.dto.game.GameResponse;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;

@Relation(collectionRelation = "stats", itemRelation = "statLastGame")
public class PlayerStatLastGameResponse extends RepresentationModel<PlayerStatLastGameResponse> {
    private final PlayerResponse player;
    private final GameResponse game;
    private final boolean isWon;
    private final int killsAmount;
    private final int deathsAmount;
    private final int damageDoneAmount;
    private final int damageReceivedAmount;
    private final int dominanceIndicator;
    private final int playerRatingAfterGame;

    public PlayerStatLastGameResponse(PlayerResponse player, GameResponse game, boolean isWon, int killsAmount, int deathsAmount, int damageDoneAmount, int damageReceivedAmount, int dominanceIndicator, int playerRatingAfterGame) {
        this.player = player;
        this.game = game;
        this.isWon = isWon;
        this.killsAmount = killsAmount;
        this.deathsAmount = deathsAmount;
        this.damageDoneAmount = damageDoneAmount;
        this.damageReceivedAmount = damageReceivedAmount;
        this.dominanceIndicator = dominanceIndicator;
        this.playerRatingAfterGame = playerRatingAfterGame;
    }

    public PlayerResponse getPlayer() {
        return player;
    }

    public GameResponse getGame() {
        return game;
    }

    public boolean isWon() {
        return isWon;
    }

    public int getKillsAmount() {
        return killsAmount;
    }

    public int getDeathsAmount() {
        return deathsAmount;
    }

    public int getDamageDoneAmount() {
        return damageDoneAmount;
    }

    public int getDamageReceivedAmount() {
        return damageReceivedAmount;
    }

    public int getDominanceIndicator() {
        return dominanceIndicator;
    }

    public int getPlayerRatingAfterGame() {
        return playerRatingAfterGame;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlayerStatLastGameResponse that = (PlayerStatLastGameResponse) o;
        return isWon == that.isWon && killsAmount == that.killsAmount && deathsAmount == that.deathsAmount && damageDoneAmount == that.damageDoneAmount && damageReceivedAmount == that.damageReceivedAmount && dominanceIndicator == that.dominanceIndicator && playerRatingAfterGame == that.playerRatingAfterGame && Objects.equals(player, that.player) && Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), player, game, isWon, killsAmount, deathsAmount, damageDoneAmount, damageReceivedAmount, dominanceIndicator, playerRatingAfterGame);
    }
}
