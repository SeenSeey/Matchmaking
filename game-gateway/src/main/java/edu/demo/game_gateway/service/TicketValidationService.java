package edu.demo.game_gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.game_gateway.dto.PlayerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketValidationService {
    private static final Logger logger = LoggerFactory.getLogger(TicketValidationService.class);
    private static final String TICKET_PREFIX = "ticket:";
    
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public TicketValidationService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public PlayerInfo validateTicket(String ticketId) {
        String key = TICKET_PREFIX + ticketId;
        String ticketData = redisTemplate.opsForValue().get(key);
        
        if (ticketData == null) {
            logger.warn("Билет не найден в Redis: ticketId={}", ticketId);
            return null;
        }

        try {
            PlayerInfo playerInfo = objectMapper.readValue(ticketData, PlayerInfo.class);
            logger.info("Билет успешно валидирован: ticketId={}, playerId={}", ticketId, playerInfo.getPlayerId());
            return playerInfo;
        } catch (Exception e) {
            logger.error("Ошибка при десериализации данных билета: ticketId={}", ticketId, e);
            return null;
        }
    }
}
