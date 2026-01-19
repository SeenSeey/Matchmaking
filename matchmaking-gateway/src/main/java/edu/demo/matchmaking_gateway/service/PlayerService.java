package edu.demo.matchmaking_gateway.service;

import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;

public interface PlayerService {

    PlayerResponse create(PlayerRequest request);

    PlayerResponse getById(Long id);

}