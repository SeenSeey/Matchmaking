package edu.demo.game_service.listener;

import com.example.events_contract.PlayerDisconnectedEvent;
import edu.demo.game_service.config.RabbitMQConfig;
import edu.demo.game_service.service.GameService;
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
public class PlayerDisconnectedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PlayerDisconnectedEventListener.class);

    private final GameService gameService;
    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

    public PlayerDisconnectedEventListener(GameService gameService) {
        this.gameService = gameService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PLAYER_DISCONNECTED_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handlePlayerDisconnectedEvent(
            PlayerDisconnectedEvent event,
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventKey = event.matchId() + "_" + event.disconnectedPlayerId() + "_" + deliveryTag;

        if (processedEvents.containsKey(eventKey)) {
            logger.warn("Дубликат события игнорирован: eventKey={}, matchId={}, playerId={}", 
                    eventKey, event.matchId(), event.disconnectedPlayerId());
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
            logger.debug("Очищено {} записей из кэша обработанных событий", removed);
        }

        logger.info("Получено событие PlayerDisconnectedEvent: matchId={}, disconnectedPlayerId={}", 
                event.matchId(), event.disconnectedPlayerId());

        try {
            gameService.finishGameOnDisconnect(event.matchId(), event.disconnectedPlayerId());
            channel.basicAck(deliveryTag, false);
            logger.debug("Событие успешно обработано: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при обработке PlayerDisconnectedEvent: eventKey={}, matchId={}, playerId={}", 
                    eventKey, event.matchId(), event.disconnectedPlayerId(), e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
