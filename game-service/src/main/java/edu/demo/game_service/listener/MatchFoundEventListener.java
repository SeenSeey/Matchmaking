package edu.demo.game_service.listener;

import com.example.events_contract.MatchFoundEvent;
import edu.demo.game_service.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MatchFoundEventListener {
    private static final Logger logger = LoggerFactory.getLogger(MatchFoundEventListener.class);

    @RabbitListener(queues = RabbitMQConfig.MATCH_FOUND_QUEUE)
    public void handleMatchFoundEvent(MatchFoundEvent event) {
        logger.info("Получено событие MatchFoundEvent из RabbitMQ: matchId={}, player1Id={}, player2Id={}, region={}", 
                event.matchId(), event.player1Id(), event.player2Id(), event.region());

        System.out.println("========================================");
        System.out.println("MatchFoundEvent получен:");
        System.out.println("  Match ID: " + event.matchId());
        System.out.println("  Player 1 ID: " + event.player1Id());
        System.out.println("  Player 2 ID: " + event.player2Id());
        System.out.println("  Region: " + event.region());
        System.out.println("========================================");
    }
}
