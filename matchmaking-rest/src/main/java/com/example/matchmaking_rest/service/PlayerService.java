package com.example.matchmaking_rest.service;

import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;

import java.util.List;

public interface PlayerService {
    PlayerResponse create(PlayerRequest request);
    PlayerResponse getById(Long id);
}
