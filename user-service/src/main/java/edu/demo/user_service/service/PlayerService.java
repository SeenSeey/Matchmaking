package edu.demo.user_service.service;

import edu.demo.user_service.model.Player;
import edu.demo.user_service.model.PlayerStatus;
import edu.demo.user_service.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(String nickname, String region, int rating) {
        Player player = new Player(UUID.randomUUID().toString(), nickname, region, rating);
        Player savedPlayer = playerRepository.save(player);
        logger.info("Игрок создан: id={}, nickname={}, region={}, rating={}", 
                savedPlayer.getId(), savedPlayer.getNickname(), savedPlayer.getRegion(), savedPlayer.getRating());
        return savedPlayer;
    }

    public Player getPlayerById(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found: " + id));
        logger.info("Игрок получен: id={}, nickname={}, region={}, rating={}", 
                player.getId(), player.getNickname(), player.getRegion(), player.getRating());
        return player;
    }

    public Player getInfoForStartGame(String id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found: " + id));
        
        if (player.getStatus() == PlayerStatus.IN_GAME) {
            throw new RuntimeException("Player is already in game: " + id);
        }
        
        player.setStatus(PlayerStatus.IN_GAME);
        Player savedPlayer = playerRepository.save(player);
        
        logger.info("Игрок переведен в статус IN_GAME: id={}, nickname={}, region={}, rating={}", 
                savedPlayer.getId(), savedPlayer.getNickname(), savedPlayer.getRegion(), savedPlayer.getRating());
        
        return savedPlayer;
    }
}
