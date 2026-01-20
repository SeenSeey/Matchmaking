package edu.demo.matchmaking_gateway.service;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.GameResponse;
import com.example.matcmaking_api.dto.game.StartGameResponse;

public interface GameService {

    StartGameResponse startGame(GameRequest request);

    void leaveGame(GameRequest request);

    GameResponse getById(Long id);

}