package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import edu.demo.matchmaking_gateway.model.Player;
import edu.demo.matchmaking_gateway.repo.InMemoryPlayerRepository;
import edu.demo.matchmaking_gateway.service.PlayerService;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final InMemoryPlayerRepository playerRepo;

    public PlayerServiceImpl(InMemoryPlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    @Override
    public PlayerResponse create(PlayerRequest request) {
        var player = new Player(null, request.nickname(), request.region(), request.rating());
        player = playerRepo.save(player);

        return new PlayerResponse(
                player.getId(),
                player.getNickname(),
                player.getRating(),
                player.getRegion()
        );
    }

    @Override
    public PlayerResponse getById(Long id) {
        var player = playerRepo.findById(id).orElseThrow(() -> new RuntimeException("player not found: " + id));

        return new PlayerResponse(
                player.getId(),
                player.getNickname(),
                player.getRating(),
                player.getRegion()
        );
    }
}