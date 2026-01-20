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

    private final RabbitTemplate rabbitTemplate;

    public MatchEventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMatchFoundEvent(
            String player1Id, String player1Nickname, int player1Rating,
            String player2Id, String player2Nickname, int player2Rating,
            String region) {
        String matchId = UUID.randomUUID().toString();
        MatchFoundEvent event = new MatchFoundEvent(
                matchId,
                player1Id, player1Nickname, player1Rating,
                player2Id, player2Nickname, player2Rating,
                region
        );
        
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.MATCH_FOUND_QUEUE, event);
            logger.info("Событие MatchFoundEvent отправлено: matchId={}, player1Id={}, player1Nickname={}, player1Rating={}, player2Id={}, player2Nickname={}, player2Rating={}, region={}", 
                    matchId, player1Id, player1Nickname, player1Rating, player2Id, player2Nickname, player2Rating, region);
        } catch (Exception e) {
            logger.error("Ошибка при отправке события MatchFoundEvent: player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
            throw new RuntimeException("Failed to publish match found event", e);
        }
    }
}
