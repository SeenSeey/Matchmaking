package edu.demo.matchmaker_service.service;

import com.example.events_contract.MatchFoundEvent;
import edu.demo.matchmaker_service.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MatchEventPublisherService {
    private static final Logger logger = LoggerFactory.getLogger(MatchEventPublisherService.class);

    private final RedisNotificationService redisNotificationService;
    private final RabbitTemplate rabbitTemplate;

    public MatchEventPublisherService(
            RedisNotificationService redisNotificationService,
            RabbitTemplate rabbitTemplate) {
        this.redisNotificationService = redisNotificationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMatchFoundEvent(String player1Id, String player2Id, String region) {
        String matchId = UUID.randomUUID().toString();
        MatchFoundEvent event = new MatchFoundEvent(matchId, player1Id, player2Id, region);

        redisNotificationService.notifyPlayers(player1Id, player2Id, matchId);

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.MATCH_FOUND_QUEUE, event);
            logger.info("Событие MatchFoundEvent отправлено в RabbitMQ: matchId={}, player1Id={}, player2Id={}, region={}", 
                    matchId, player1Id, player2Id, region);
        } catch (Exception e) {
            logger.error("Ошибка при отправке MatchFoundEvent в RabbitMQ: player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
            throw new RuntimeException("Failed to publish match found event", e);
        }
    }
}
