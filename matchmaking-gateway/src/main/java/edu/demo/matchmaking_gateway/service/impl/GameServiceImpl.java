package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.GameResponse;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.example.matcmaking_api.exception.ResourceNotFoundException;
import edu.demo.matchmaking_gateway.model.Game;
import edu.demo.matchmaking_gateway.model.Player;
import edu.demo.matchmaking_gateway.repo.InMemoryGameRepository;
import edu.demo.matchmaking_gateway.repo.InMemoryPlayerRepository;
import edu.demo.matchmaking_gateway.service.GameService;
import edu.demo.matchmaking_gateway.service.PlayerService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Random;

@Service
public class GameServiceImpl implements GameService {
    private final InMemoryGameRepository gameRepo;
    private final InMemoryPlayerRepository playerRepo;
    private final PlayerService playerService;
    private static final String[] ROOM_MAPS = {"Map1", "Map2", "Map3", "Map4", "Map5"};
    private final Random random = new Random();

    public GameServiceImpl(InMemoryGameRepository gameRepo, InMemoryPlayerRepository playerRepo, PlayerService playerService) {
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.playerService = playerService;
    }

    @Override
    public GameResponse startGame(GameRequest request) {
        Player firstPlayer = playerRepo.findById(request.playerId())
                .orElseThrow(() -> new ResourceNotFoundException("Игрок", request.playerId()));

        gameRepo.findByPlayerId(request.playerId())
                .ifPresent(game -> {
                    throw new RuntimeException("player already in game: " + request.playerId());
                });

        Player secondPlayer = findOpponent(request.playerId(), firstPlayer.getRegion(), firstPlayer.getRating());

        String roomMap = ROOM_MAPS[random.nextInt(ROOM_MAPS.length)];
        Timestamp gameStartedTime = new Timestamp(System.currentTimeMillis());
        Game game = new Game(null, firstPlayer.getId(), secondPlayer.getId(), roomMap, gameStartedTime);
        game = gameRepo.save(game);

        PlayerResponse firstPlayerResponse = playerService.getById(firstPlayer.getId());
        PlayerResponse secondPlayerResponse = playerService.getById(secondPlayer.getId());

        return new GameResponse(
                game.getId(),
                firstPlayerResponse,
                secondPlayerResponse,
                game.getRoomMap(),
                game.getGameStartedTime()
        );
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