package edu.demo.audit_service.listener;

import com.example.events_contract.*;
import edu.demo.audit_service.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuditEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AuditEventListener.class);
    private final ConcurrentHashMap<String, Boolean> processedMessages = new ConcurrentHashMap<>();

    @RabbitListener(queues = "audit." + RabbitMQConfig.PLAYER_SEARCHING_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handlePlayerSearchingOpponentEvent(PlayerSearchingOpponentEvent event, Message message, Channel channel) {
        String messageKey = "player.searching." + event.playerId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: PlayerSearchingOpponentEvent - playerId={}, nickname={}, rating={}, region={}", 
                event.playerId(), event.nickname(), event.rating(), event.region());
        acknowledgeMessage(channel, message);
    }

    @RabbitListener(queues = "audit." + RabbitMQConfig.MATCH_FOUND_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handleMatchFoundEvent(MatchFoundEvent event, Message message, Channel channel) {
        String messageKey = "match.found." + event.matchId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: MatchFoundEvent - matchId={}, player1Id={}, player2Id={}, region={}", 
                event.matchId(), event.player1Id(), event.player2Id(), event.region());
        acknowledgeMessage(channel, message);
    }

    @RabbitListener(queues = "audit." + RabbitMQConfig.PLAYER_LEFT_QUEUE_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handlePlayerLeftQueueEvent(PlayerLeftQueueEvent event, Message message, Channel channel) {
        String messageKey = "player.left.queue." + event.playerId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: PlayerLeftQueueEvent - playerId={}, region={}", 
                event.playerId(), event.region());
        acknowledgeMessage(channel, message);
    }

    @RabbitListener(queues = "audit." + RabbitMQConfig.GAME_FINISHED_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handleGameFinishedEvent(GameFinishedEvent event, Message message, Channel channel) {
        String messageKey = "game.finished." + event.matchId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: GameFinishedEvent - matchId={}, player1Id={}, player1Damage={}, player2Id={}, player2Damage={}, winnerId={}, region={}", 
                event.matchId(), event.player1Id(), event.player1Damage(), event.player2Id(), 
                event.player2Damage(), event.winnerId(), event.region());
        acknowledgeMessage(channel, message);
    }

    @RabbitListener(queues = "audit." + RabbitMQConfig.PLAYER_DISCONNECTED_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handlePlayerDisconnectedEvent(PlayerDisconnectedEvent event, Message message, Channel channel) {
        String messageKey = "player.disconnected." + event.matchId() + "_" + event.disconnectedPlayerId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: PlayerDisconnectedEvent - matchId={}, disconnectedPlayerId={}", 
                event.matchId(), event.disconnectedPlayerId());
        acknowledgeMessage(channel, message);
    }

    @RabbitListener(queues = "audit." + RabbitMQConfig.PLAYER_STATS_UPDATED_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handlePlayerStatsUpdatedEvent(PlayerStatsUpdatedEvent event, Message message, Channel channel) {
        String messageKey = "player.stats.updated." + event.playerId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: PlayerStatsUpdatedEvent - playerId={}, rating={}, winsCount={}, lossesCount={}", 
                event.playerId(), event.rating(), event.winsCount(), event.lossesCount());
        acknowledgeMessage(channel, message);
    }

    @RabbitListener(queues = "audit." + RabbitMQConfig.PLAYER_STATUS_UPDATE_QUEUE, 
                    containerFactory = "rabbitListenerContainerFactory")
    public void handlePlayerStatusUpdateEvent(PlayerStatusUpdateEvent event, Message message, Channel channel) {
        String messageKey = "player.status.update." + event.playerId() + "_" + message.getMessageProperties().getDeliveryTag();
        
        if (processedMessages.putIfAbsent(messageKey, true) != null) {
            logger.warn("Duplicate message detected: {}", messageKey);
            acknowledgeMessage(channel, message);
            return;
        }

        logger.info("AUDIT: PlayerStatusUpdateEvent - playerId={}, status={}", 
                event.playerId(), event.status());
        acknowledgeMessage(channel, message);
    }

    private void acknowledgeMessage(Channel channel, Message message) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            logger.error("Error acknowledging message", e);
        }
    }
}
