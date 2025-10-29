package com.example.matchmaking_rest.graphql;

import com.example.matchmaking_rest.service.PlayerService;
import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.Map;

@DgsComponent
public class PlayerDataFetcher {

    private final PlayerService playerService;

    public PlayerDataFetcher(PlayerService playerService) {
        this.playerService = playerService;
    }

    @DgsMutation
    public PlayerResponse createPlayer(@InputArgument("input") Map<String, Object> input) {
        PlayerRequest request = new PlayerRequest(
                (String) input.get("username"),
                (String) input.get("region"),
                ((Number) input.get("rating")).intValue()
        );
        return playerService.create(request);
    }


    @DgsQuery
    public PlayerResponse playerById(@InputArgument Long id) {
        return playerService.getById(id);
    }
}
