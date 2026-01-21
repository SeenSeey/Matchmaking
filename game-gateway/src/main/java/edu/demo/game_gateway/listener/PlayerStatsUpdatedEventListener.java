package edu.demo.game_gateway.listener;

import com.example.events_contract.PlayerStatsUpdatedEvent;
import edu.demo.game_gateway.config.RabbitMQConfig;
import edu.demo.game_gateway.handler.NotificationWebSocketHandler;
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
public class PlayerStatsUpdatedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PlayerStatsUpdatedEventListener.class);

    private final NotificationWebSocketHandler webSocketHandler;
    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

    public PlayerStatsUpdatedEventListener(NotificationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PLAYER_STATS_UPDATED_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handlePlayerStatsUpdatedEvent(
            PlayerStatsUpdatedEvent event,
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
            logger.debug("Очищено {} записей из кэша обработанных событий", removed);
        }

        logger.info("Получено событие PlayerStatsUpdatedEvent: playerId={}, rating={}, wins={}, losses={}", 
                event.playerId(), event.rating(), event.winsCount(), event.lossesCount());

        try {
            webSocketHandler.sendStatsAndCloseConnection(event);
            channel.basicAck(deliveryTag, false);
            logger.debug("Событие успешно обработано: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при обработке PlayerStatsUpdatedEvent: eventKey={}, playerId={}", 
                    eventKey, event.playerId(), e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
