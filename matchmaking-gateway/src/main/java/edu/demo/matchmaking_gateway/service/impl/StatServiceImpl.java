package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.game.GameResponse;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatAllTimeResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatLastGameResponse;
import com.example.matcmaking_api.exception.ResourceNotFoundException;
import edu.demo.matchmaking_gateway.model.StatAllTime;
import edu.demo.matchmaking_gateway.model.StatGame;
import edu.demo.matchmaking_gateway.repo.InMemoryStatRepository;
import edu.demo.matchmaking_gateway.service.GameService;
import edu.demo.matchmaking_gateway.service.PlayerService;
import edu.demo.matchmaking_gateway.service.StatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatServiceImpl implements StatService {
    private final InMemoryStatRepository statRepo;
    private final PlayerService playerService;
    private final GameService gameService;

    public StatServiceImpl(InMemoryStatRepository statRepo, PlayerService playerService, GameService gameService) {
        this.statRepo = statRepo;
        this.playerService = playerService;
        this.gameService = gameService;
    }

    @Override
    public PlayerStatLastGameResponse getPlayerStatLastGame(Long playerId) {
        StatGame statGame = statRepo.findLastGameStatByPlayerId(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Статистика за последнюю игру", playerId));

        PlayerResponse player = playerService.getById(playerId);
        GameResponse game = gameService.getById(statGame.getGameId());

        return new PlayerStatLastGameResponse(
                player,
                game,
                statGame.isWon(),
                statGame.getKillsAmount(),
                statGame.getDeathsAmount(),
                statGame.getDamageDoneAmount(),
                statGame.getDamageReceivedAmount(),
                statGame.getDominanceIndicator(),
                statGame.getPlayerRatingAfterGame()
        );
    }

    @Override
    public PlayerStatAllTimeResponse getPlayerStatAllTime(Long playerId) {
        StatAllTime statAllTime = statRepo.findAllTimeStatByPlayerId(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Статистика за всё время", playerId));

        PlayerResponse player = playerService.getById(playerId);

        return new PlayerStatAllTimeResponse(
                player,
                statAllTime.getWinsAmount(),
                statAllTime.getDefeatsAmount()
        );
    }

    @Override
    public Page<PlayerStatAllTimeResponse> getTopPlayersPages(int page, int size) {
        List<StatAllTime> stats = statRepo.findAllTimeStatsSortedByWins(page, size);
        long total = statRepo.countAllTimeStats();

        List<PlayerStatAllTimeResponse> responses = stats.stream()
                .map(stat -> {
                    PlayerResponse player = playerService.getById(stat.getPlayerId());
                    return new PlayerStatAllTimeResponse(
                            player,
                            stat.getWinsAmount(),
                            stat.getDefeatsAmount()
                    );
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responses, pageable, total);
    }
}