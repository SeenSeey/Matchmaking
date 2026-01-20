package edu.demo.matchmaker_service.listener;

import com.example.events_contract.PlayerSearchingOpponentEvent;
import edu.demo.matchmaker_service.config.RabbitMQConfig;
import edu.demo.matchmaker_service.service.MatchEventPublisherService;
import edu.demo.matchmaker_service.service.MatchmakingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlayerSearchingOpponentListener {
    private static final Logger logger = LoggerFactory.getLogger(PlayerSearchingOpponentListener.class);

    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();
    private final MatchmakingService matchmakingService;
    private final MatchEventPublisherService matchEventPublisherService;

    public PlayerSearchingOpponentListener(
            MatchmakingService matchmakingService,
            MatchEventPublisherService matchEventPublisherService) {
        this.matchmakingService = matchmakingService;
        this.matchEventPublisherService = matchEventPublisherService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PLAYER_SEARCHING_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handlePlayerSearchingOpponentEvent(
            PlayerSearchingOpponentEvent event, 
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventKey = event.playerId() + "_" + deliveryTag;

        if (processedEvents.containsKey(eventKey)) {
            logger.warn("Дубликат события игнорирован: eventKey={}, playerId={}", eventKey, event.playerId());
            channel.basicAck(deliveryTag, false);
            return;
        }

        processedEvents.put(eventKey, true);

        if (processedEvents.size() > 10000) {
            int removed = 0;
            for (String key : processedEvents.keySet()) {
                if (removed >= 1000) break;
                processedEvents.remove(key);
                removed++;
            }
        }

        logger.info("Получено событие PlayerSearchingOpponentEvent: playerId={}, nickname={}, rating={}, region={}", 
                event.playerId(), event.nickname(), event.rating(), event.region());

        try {
            // Сохраняем информацию об игроке в кэш перед добавлением в пул
            matchmakingService.cachePlayerInfo(
                    event.playerId(),
                    event.nickname(),
                    event.rating(),
                    event.region()
            );

            edu.demo.matchmaker_service.dto.PlayerInfo[] pair = matchmakingService.findAndRemovePair(
                    event.playerId(), 
                    event.rating(), 
                    event.region()
            );

            if (pair != null && pair.length == 2) {
                edu.demo.matchmaker_service.dto.PlayerInfo player1 = pair[0];
                edu.demo.matchmaker_service.dto.PlayerInfo player2 = pair[1];
                
                logger.info("Пара найдена: player1Id={}, player2Id={}, region={}", 
                        player1.getPlayerId(), player2.getPlayerId(), event.region());

                matchEventPublisherService.publishMatchFoundEvent(
                        player1.getPlayerId(),
                        player1.getNickname(),
                        player1.getRating(),
                        player2.getPlayerId(),
                        player2.getNickname(),
                        player2.getRating(),
                        event.region()
                );
            } else {
                logger.info("Пара не найдена для игрока: playerId={}, rating={}, region={}. Игрок добавлен в пул ожидания.", 
                        event.playerId(), event.rating(), event.region());
            }

            channel.basicAck(deliveryTag, false);
            logger.debug("Событие подтверждено: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при обработке события: eventKey={}", eventKey, e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
