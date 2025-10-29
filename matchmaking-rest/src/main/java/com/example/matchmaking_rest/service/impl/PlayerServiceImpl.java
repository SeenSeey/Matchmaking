package com.example.matchmaking_rest.service.impl;

import com.example.matchmaking_rest.model.Player;
import com.example.matchmaking_rest.repository.InMemoryPlayerRepository;
import com.example.matchmaking_rest.service.PlayerService;
import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final InMemoryPlayerRepository repo;

    public PlayerServiceImpl(InMemoryPlayerRepository repo) {
        this.repo = repo;
    }

    @Override
    public PlayerResponse create(PlayerRequest request) {
        var player = new Player(null, request.username(), request.region(), request.rating());
        player = repo.save(player);

        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getRating(),
                player.getRegion()
        );
    }

    @Override
    public PlayerResponse getById(Long id) {
        var player = repo.findById(id).orElseThrow(() -> new RuntimeException("player not found: " + id));

        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getRating(),
                player.getRegion()
        );
    }
}
