package edu.demo.matchmaker_service.listener;

import com.example.events_contract.PlayerLeftQueueEvent;
import edu.demo.matchmaker_service.config.RabbitMQConfig;
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
public class PlayerLeftQueueEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PlayerLeftQueueEventListener.class);

    private final MatchmakingService matchmakingService;
    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

    public PlayerLeftQueueEventListener(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PLAYER_LEFT_QUEUE_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handlePlayerLeftQueueEvent(
            PlayerLeftQueueEvent event,
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventKey = event.playerId() + "_" + event.region() + "_" + deliveryTag;

        if (processedEvents.containsKey(eventKey)) {
            logger.warn("Дубликат события игнорирован: eventKey={}, playerId={}, region={}", 
                    eventKey, event.playerId(), event.region());
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

        logger.info("Получено событие PlayerLeftQueueEvent: playerId={}, region={}", 
                event.playerId(), event.region());

        try {
            matchmakingService.removePlayerFromQueue(event.playerId(), event.region());
            channel.basicAck(deliveryTag, false);
            logger.debug("Событие успешно обработано: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при обработке PlayerLeftQueueEvent: eventKey={}, playerId={}, region={}", 
                    eventKey, event.playerId(), event.region(), e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
