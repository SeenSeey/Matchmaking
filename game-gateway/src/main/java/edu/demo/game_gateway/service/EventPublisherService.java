package edu.demo.game_gateway.service;

import com.example.events_contract.PlayerDisconnectedEvent;
import com.example.events_contract.PlayerSearchingOpponentEvent;
import edu.demo.game_gateway.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    
    private final RabbitTemplate rabbitTemplate;

    public EventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPlayerSearchingOpponentEvent(String playerId, String nickname, int rating, String region) {
        PlayerSearchingOpponentEvent event = new PlayerSearchingOpponentEvent(playerId, nickname, rating, region);
        
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.PLAYER_SEARCHING_QUEUE, event);
            logger.info("Событие PlayerSearchingOpponentEvent отправлено: playerId={}, nickname={}, rating={}, region={}", 
                    playerId, nickname, rating, region);
        } catch (Exception e) {
            logger.error("Ошибка при отправке события PlayerSearchingOpponentEvent: playerId={}", playerId, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    public void publishPlayerDisconnectedEvent(String matchId, String playerId) {
        PlayerDisconnectedEvent event = new PlayerDisconnectedEvent(matchId, playerId);
        
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.PLAYER_DISCONNECTED_QUEUE, event);
            logger.info("Событие PlayerDisconnectedEvent отправлено: matchId={}, playerId={}", matchId, playerId);
        } catch (Exception e) {
            logger.error("Ошибка при отправке события PlayerDisconnectedEvent: matchId={}, playerId={}", 
                    matchId, playerId, e);
            throw new RuntimeException("Failed to publish player disconnected event", e);
        }
    }
}
