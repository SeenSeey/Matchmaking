package edu.demo.user_service.listener;

import com.example.events_contract.PlayerStatusUpdateEvent;
import edu.demo.user_service.config.RabbitMQConfig;
import edu.demo.user_service.model.PlayerStatus;
import edu.demo.user_service.repository.PlayerRepository;
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
public class PlayerStatusUpdateEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PlayerStatusUpdateEventListener.class);

    private final PlayerRepository playerRepository;
    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

    public PlayerStatusUpdateEventListener(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @RabbitListener(
            queues = RabbitMQConfig.PLAYER_STATUS_UPDATE_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handlePlayerStatusUpdateEvent(
            PlayerStatusUpdateEvent event,
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventKey = event.playerId() + "_" + event.status() + "_" + deliveryTag;

        if (processedEvents.containsKey(eventKey)) {
            logger.warn("Дубликат события игнорирован: eventKey={}, playerId={}, status={}", 
                    eventKey, event.playerId(), event.status());
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

        logger.info("Получено событие PlayerStatusUpdateEvent: playerId={}, status={}", 
                event.playerId(), event.status());

        try {
            playerRepository.findById(event.playerId())
                    .ifPresentOrElse(
                            player -> {
                                try {
                                    PlayerStatus newStatus = PlayerStatus.valueOf(event.status());
                                    player.setStatus(newStatus);
                                    playerRepository.save(player);
                                    logger.info("Статус игрока обновлен: playerId={}, новый статус={}", 
                                            event.playerId(), newStatus);
                                } catch (IllegalArgumentException e) {
                                    logger.error("Неверный статус в событии: playerId={}, status={}", 
                                            event.playerId(), event.status(), e);
                                }
                            },
                            () -> logger.warn("Игрок не найден для обновления статуса: playerId={}", 
                                    event.playerId())
                    );

            channel.basicAck(deliveryTag, false);
            logger.debug("Событие успешно обработано: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при обработке PlayerStatusUpdateEvent: eventKey={}, playerId={}, status={}", 
                    eventKey, event.playerId(), event.status(), e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
