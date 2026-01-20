package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.GameResponse;
import com.example.matcmaking_api.dto.game.StartGameResponse;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.example.matcmaking_api.exception.ResourceNotFoundException;
import edu.demo.matchmaking_gateway.model.Game;
import edu.demo.matchmaking_gateway.model.Player;
import edu.demo.matchmaking_gateway.repo.InMemoryGameRepository;
import edu.demo.matchmaking_gateway.repo.InMemoryPlayerRepository;
import edu.demo.matchmaking_gateway.service.GameService;
import edu.demo.matchmaking_gateway.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    private final InMemoryGameRepository gameRepo;
    private final InMemoryPlayerRepository playerRepo;
    private final PlayerService playerService;

    public GameServiceImpl(InMemoryGameRepository gameRepo, InMemoryPlayerRepository playerRepo, PlayerService playerService) {
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.playerService = playerService;
    }

    @Override
    public StartGameResponse startGame(GameRequest request) {
        PlayerResponse playerInfo = playerService.getInfoForStartGame(request.playerId());

        String gameKey = UUID.randomUUID().toString();
        
        logger.info("Игра начата: playerId={}, gameKey={}, nickname={}, region={}, rating={}", 
                playerInfo.getPlayerId(), gameKey, playerInfo.getNickname(), 
                playerInfo.getRegion(), playerInfo.getRating());
        
        return new StartGameResponse(gameKey);
    }

    @Override
    public void leaveGame(GameRequest request) {
        gameRepo.findByPlayerId(request.playerId())
                .orElseThrow(() -> new ResourceNotFoundException("Игра для игрока", request.playerId()));
    }

    @Override
    public GameResponse getById(Long id) {
        Game game = gameRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Игра", id));

        PlayerResponse firstPlayerResponse = playerService.getById(game.getFirstPlayerId());
        PlayerResponse secondPlayerResponse = playerService.getById(game.getSecondPlayerId());

        return new GameResponse(
                game.getId(),
                firstPlayerResponse,
                secondPlayerResponse,
                game.getRoomMap(),
                game.getGameStartedTime()
        );
    }

    private Player findOpponent(String currentPlayerId, String region, int rating) {
        return playerRepo.findAll().stream()
                .filter(player -> !player.getId().equals(currentPlayerId))
                .filter(player -> player.getRegion().equals(region))
                .filter(player -> gameRepo.findByPlayerId(player.getId()).isEmpty())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no opponent found"));
    }
}