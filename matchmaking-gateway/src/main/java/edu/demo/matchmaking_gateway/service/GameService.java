package edu.demo.matchmaking_gateway.service;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.GameResponse;

public interface GameService {

    GameResponse startGame(GameRequest request);

    void leaveGame(GameRequest request);

    GameResponse getById(Long id);

}