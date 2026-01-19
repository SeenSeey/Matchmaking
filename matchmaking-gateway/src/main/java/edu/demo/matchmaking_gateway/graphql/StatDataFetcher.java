package edu.demo.matchmaking_gateway.graphql;

import com.example.matcmaking_api.dto.stat.PlayerStatAllTimeResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatLastGameResponse;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.demo.matchmaking_gateway.service.StatService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@DgsComponent
public class StatDataFetcher {
    private final StatService statService;

    public StatDataFetcher(StatService statService) {
        this.statService = statService;
    }

    @DgsQuery
    public PlayerStatLastGameResponse playerStatLastGame(@InputArgument String playerId) {
        return statService.getPlayerStatLastGame(playerId);
    }

    @DgsQuery
    public PlayerStatAllTimeResponse playerStatAllTime(@InputArgument String playerId) {
        return statService.getPlayerStatAllTime(playerId);
    }

    @DgsQuery
    public Map<String, Object> topPlayers(@InputArgument Integer page,
                                          @InputArgument Integer size) {
        int pageValue = page != null ? page : 0;
        int sizeValue = size != null ? size : 20;
        Page<PlayerStatAllTimeResponse> pageResult = statService.getTopPlayersPages(pageValue, sizeValue);
        
        List<PlayerStatAllTimeResponse> content = pageResult.getContent();
        
        return Map.of(
                "content", content,
                "pageNumber", pageResult.getNumber(),
                "pageSize", pageResult.getSize(),
                "totalElements", pageResult.getTotalElements(),
                "totalPages", pageResult.getTotalPages(),
                "last", pageResult.isLast()
        );
    }
}