package edu.demo.matchmaking_gateway.graphql;

import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.demo.matchmaking_gateway.service.PlayerService;

import java.util.Map;

@DgsComponent
public class PlayerDataFetcher {
    private final PlayerService playerService;

    public PlayerDataFetcher(PlayerService playerService) {
        this.playerService = playerService;
    }

    @DgsMutation
    public PlayerResponse registerPlayer(@InputArgument("input") Map<String, Object> input) {
        PlayerRequest request = new PlayerRequest(
                (String) input.get("nickname"),
                (String) input.get("region"),
                ((Number) input.get("rating")).intValue()
        );
        return playerService.create(request);
    }

    @DgsQuery
    public PlayerResponse playerById(@InputArgument Long playerId) {
        return playerService.getById(playerId);
    }
}