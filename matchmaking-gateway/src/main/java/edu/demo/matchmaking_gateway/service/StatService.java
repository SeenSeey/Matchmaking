package edu.demo.matchmaking_gateway.service;

import com.example.matcmaking_api.dto.stat.PlayerStatAllTimeResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatLastGameResponse;
import org.springframework.data.domain.Page;

public interface StatService {

    PlayerStatLastGameResponse getPlayerStatLastGame(String playerId);

    PlayerStatAllTimeResponse getPlayerStatAllTime(String playerId);

    Page<PlayerStatAllTimeResponse> getTopPlayersPages(int page, int size);

}