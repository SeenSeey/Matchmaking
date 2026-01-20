package edu.demo.matchmaker_service.listener;

import com.example.events_contract.PlayerSearchingOpponentEvent;
import edu.demo.matchmaker_service.config.RabbitMQConfig;
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
            channel.basicAck(deliveryTag, false);
            logger.debug("Событие подтверждено: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при подтверждении события: eventKey={}", eventKey, e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
