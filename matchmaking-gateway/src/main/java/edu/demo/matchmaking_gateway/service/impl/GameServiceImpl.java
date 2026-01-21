package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.StartGameResponse;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import edu.demo.matchmaking_gateway.service.GameService;
import edu.demo.matchmaking_gateway.service.PlayerService;
import edu.demo.matchmaking_gateway.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    private final PlayerService playerService;
    private final TicketService ticketService;
    
    @Value("${game.gateway.url:ws://localhost:8083/ws}")
    private String gameGatewayUrl;

    public GameServiceImpl(PlayerService playerService, TicketService ticketService) {
        this.playerService = playerService;
        this.ticketService = ticketService;
    }

    @Override
    public StartGameResponse startGame(GameRequest request) {
        PlayerResponse playerInfo = playerService.getInfoForStartGame(request.playerId());

        String gameKey = ticketService.saveTicket(playerInfo);
        
        logger.info("Игра начата: playerId={}, gameKey={}, nickname={}, region={}, rating={}, gameGatewayUrl={}", 
                playerInfo.getPlayerId(), gameKey, playerInfo.getNickname(), 
                playerInfo.getRegion(), playerInfo.getRating(), gameGatewayUrl);
        
        return new StartGameResponse(gameKey, gameGatewayUrl);
    }
}