package edu.demo.game_gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.game_gateway.dto.PlayerInfo;
import edu.demo.game_gateway.service.EventPublisherService;
import edu.demo.game_gateway.service.TicketValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    
    private final TicketValidationService ticketValidationService;
    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ObjectMapper objectMapper;
    private final EventPublisherService eventPublisherService;

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToPlayerId = new ConcurrentHashMap<>();

    public NotificationWebSocketHandler(TicketValidationService ticketValidationService,
                                      StringRedisTemplate redisTemplate,
                                      RedisMessageListenerContainer redisMessageListenerContainer,
                                      ObjectMapper objectMapper,
                                      EventPublisherService eventPublisherService) {
        this.ticketValidationService = ticketValidationService;
        this.redisTemplate = redisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
        this.objectMapper = objectMapper;
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("WebSocket соединение установлено: sessionId={}", session.getId());

        String ticketId = getTicketIdFromSession(session);
        
        if (ticketId == null || ticketId.isEmpty()) {
            logger.warn("TicketId не найден в параметрах сессии: sessionId={}", session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("Ticket ID is required"));
            return;
        }

        PlayerInfo playerInfo = ticketValidationService.validateTicket(ticketId);
        
        if (playerInfo == null) {
            logger.warn("Билет не найден или истек: ticketId={}, sessionId={}", ticketId, session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("Invalid or expired ticket"));
            return;
        }

        activeSessions.put(ticketId, session);
        sessionToPlayerId.put(session.getId(), playerInfo.getPlayerId());

        String topic = "user_notifications_" + playerInfo.getPlayerId();
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String messageText = new String(message.getBody());
                    logger.info("Получено сообщение из Redis Pub/Sub: topic={}, message={}, sessionId={}", 
                            topic, messageText, session.getId());
                    sendMessage(session, messageText);
                } catch (Exception e) {
                    logger.error("Ошибка при обработке сообщения из Redis Pub/Sub: topic={}", topic, e);
                }
            }
        };
        
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(topic));
        logger.info("Подписка на Redis топик создана: topic={}, sessionId={}, playerId={}", 
                topic, session.getId(), playerInfo.getPlayerId());

        String connectionMessage = "Соединение установлено";
        sendMessage(session, connectionMessage);
        logger.info("Соединение подтверждено: sessionId={}, playerId={}, ticketId={}", 
                session.getId(), playerInfo.getPlayerId(), ticketId);

        eventPublisherService.publishPlayerSearchingOpponentEvent(
                playerInfo.getPlayerId(),
                playerInfo.getNickname(),
                playerInfo.getRating(),
                playerInfo.getRegion()
        );

        String searchingMessage = "Поиск соперника";
        sendMessage(session, searchingMessage);
        logger.info("Сообщение 'Поиск соперника' отправлено пользователю: sessionId={}, playerId={}",
                session.getId(), playerInfo.getPlayerId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Получено сообщение от клиента: sessionId={}, message={}", 
                session.getId(), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        String playerId = sessionToPlayerId.remove(sessionId);

        activeSessions.entrySet().removeIf(entry -> entry.getValue().getId().equals(sessionId));
        
        logger.info("WebSocket соединение закрыто: sessionId={}, playerId={}, status={}", 
                sessionId, playerId, status);
    }

    private String getTicketIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("ticket=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("ticket=")) {
                    return param.substring("ticket=".length());
                }
            }
        }
        return null;
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            logger.error("Ошибка при отправке сообщения: sessionId={}", session.getId(), e);
        }
    }
}
