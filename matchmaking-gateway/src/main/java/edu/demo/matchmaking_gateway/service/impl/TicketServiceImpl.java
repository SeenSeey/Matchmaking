package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.matchmaking_gateway.repository.TicketRepository;
import edu.demo.matchmaking_gateway.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    private static final int TICKET_EXPIRATION_SECONDS = 60;
    
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    public TicketServiceImpl(TicketRepository ticketRepository, ObjectMapper objectMapper) {
        this.ticketRepository = ticketRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String saveTicket(PlayerResponse playerInfo) {
        String ticketId = UUID.randomUUID().toString();
        try {
            String jsonData = objectMapper.writeValueAsString(playerInfo);
            ticketRepository.save(ticketId, jsonData, TICKET_EXPIRATION_SECONDS);
            logger.info("Билет сохранен: ticketId={}, playerId={}, expiresIn={}s", 
                    ticketId, playerInfo.getPlayerId(), TICKET_EXPIRATION_SECONDS);
            return ticketId;
        } catch (JsonProcessingException e) {
            logger.error("Ошибка при сериализации данных игрока для билета: ticketId={}", ticketId, e);
            throw new RuntimeException("Failed to save ticket", e);
        }
    }

    @Override
    public String getTicket(String ticketId) {
        String ticketData = ticketRepository.findByTicketId(ticketId);
        if (ticketData != null) {
            logger.info("Билет найден: ticketId={}", ticketId);
        } else {
            logger.warn("Билет не найден: ticketId={}", ticketId);
        }
        return ticketData;
    }
}
