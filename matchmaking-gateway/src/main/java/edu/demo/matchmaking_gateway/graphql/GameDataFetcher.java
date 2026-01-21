package edu.demo.matchmaking_gateway.graphql;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.StartGameResponse;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import edu.demo.matchmaking_gateway.service.GameService;

import java.util.Map;

@DgsComponent
public class GameDataFetcher {
    private final GameService gameService;

    public GameDataFetcher(GameService gameService) {
        this.gameService = gameService;
    }

    @DgsMutation
    public StartGameResponse startGame(@InputArgument("input") Map<String, Object> input) {
        Object playerIdObj = input.get("playerId");
        String playerId = playerIdObj != null ? playerIdObj.toString() : null;
        GameRequest request = new GameRequest(playerId);
        return gameService.startGame(request);
    }
}