package edu.demo.game_gateway.handler;

import com.example.events_contract.PlayerStatsUpdatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.game_gateway.dto.GameMessage;
import edu.demo.game_gateway.dto.PlayerInfo;
import edu.demo.game_gateway.service.EventPublisherService;
import edu.demo.game_gateway.service.MessageFormatterService;
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
    private final MessageFormatterService messageFormatterService;

    private final Map<String, PlayerSessionInfo> sessionIdToInfo = new ConcurrentHashMap<>();
    private final Map<String, PlayerSessionInfo> playerIdToInfo = new ConcurrentHashMap<>();
    private final Map<String, Map<String, MessageListener>> gameUpdatesSubscriptions = new ConcurrentHashMap<>();

    public NotificationWebSocketHandler(TicketValidationService ticketValidationService,
                                      StringRedisTemplate redisTemplate,
                                      RedisMessageListenerContainer redisMessageListenerContainer,
                                      ObjectMapper objectMapper,
                                      EventPublisherService eventPublisherService,
                                      MessageFormatterService messageFormatterService) {
        this.ticketValidationService = ticketValidationService;
        this.redisTemplate = redisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
        this.objectMapper = objectMapper;
        this.eventPublisherService = eventPublisherService;
        this.messageFormatterService = messageFormatterService;
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

        PlayerSessionInfo sessionInfo = new PlayerSessionInfo(
                playerInfo.getPlayerId(),
                ticketId,
                session,
                playerInfo.getRegion()
        );

        String sessionId = session.getId();
        sessionIdToInfo.put(sessionId, sessionInfo);
        playerIdToInfo.put(playerInfo.getPlayerId(), sessionInfo);

        String topic = "user_notifications_" + playerInfo.getPlayerId();
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String rawMessage = new String(message.getBody());
                    logger.info("Получено сообщение из Redis Pub/Sub: topic={}, message={}, sessionId={}", 
                            topic, rawMessage, session.getId());

                    GameMessage formattedMessage = messageFormatterService.formatMessage(rawMessage, topic);
                    sendFormattedMessage(session, formattedMessage);

                    if (formattedMessage.getType() == GameMessage.MessageType.MATCH_FOUND) {
                        Object data = formattedMessage.getData();
                        if (data instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, String> dataMap = (Map<String, String>) data;
                            String matchId = dataMap.get("matchId");
                            if (matchId != null) {
                                PlayerSessionInfo sessionInfo = sessionIdToInfo.get(session.getId());
                                if (sessionInfo != null) {
                                    sessionInfo.setMatchId(matchId);
                                    logger.info("Связь playerId -> matchId установлена: playerId={}, matchId={}", 
                                            sessionInfo.getPlayerId(), matchId);
                                }
                                subscribeToGameUpdates(session, matchId);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Ошибка при обработке сообщения из Redis Pub/Sub: topic={}", topic, e);
                }
            }
        };
        
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(topic));
        logger.info("Подписка на Redis топик создана: topic={}, sessionId={}, playerId={}", 
                topic, session.getId(), playerInfo.getPlayerId());

        GameMessage connectionMessage = messageFormatterService.createConnectionMessage();
        sendFormattedMessage(session, connectionMessage);
        logger.info("Соединение подтверждено: sessionId={}, playerId={}, ticketId={}", 
                session.getId(), playerInfo.getPlayerId(), ticketId);

        eventPublisherService.publishPlayerSearchingOpponentEvent(
                playerInfo.getPlayerId(),
                playerInfo.getNickname(),
                playerInfo.getRating(),
                playerInfo.getRegion()
        );

        GameMessage searchingMessage = messageFormatterService.createSearchingMessage();
        sendFormattedMessage(session, searchingMessage);
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
        PlayerSessionInfo sessionInfo = sessionIdToInfo.remove(sessionId);

        if (sessionInfo != null) {
            String playerId = sessionInfo.getPlayerId();
            playerIdToInfo.remove(playerId);
            
            String matchId = sessionInfo.getMatchId();
            String region = sessionInfo.getRegion();

            gameUpdatesSubscriptions.forEach((matchIdKey, listeners) -> {
                if (listeners.containsKey(sessionId)) {
                    unsubscribeFromGameUpdates(sessionId, matchIdKey);
                }
            });

            String reason = status.getReason();
            if (reason != null && reason.equals("Game finished")) {
                logger.info("Соединение закрыто после завершения игры: sessionId={}, playerId={}, matchId={}, status={}", 
                        sessionId, playerId, matchId, status);
            } else if (matchId != null) {
                logger.warn("Игрок отключился во время игры: sessionId={}, playerId={}, matchId={}, status={}", 
                        sessionId, playerId, matchId, status);
                try {
                    eventPublisherService.publishPlayerDisconnectedEvent(matchId, playerId);
                    logger.info("Событие PlayerDisconnectedEvent отправлено: matchId={}, playerId={}", 
                            matchId, playerId);
                } catch (Exception e) {
                    logger.error("Ошибка при отправке события PlayerDisconnectedEvent: matchId={}, playerId={}", 
                            matchId, playerId, e);
                }
            } else if (region != null) {
                logger.info("Игрок отключился во время поиска: sessionId={}, playerId={}, region={}, status={}", 
                        sessionId, playerId, region, status);
                try {
                    eventPublisherService.publishPlayerLeftQueueEvent(playerId, region);
                    logger.info("Событие PlayerLeftQueueEvent отправлено: playerId={}, region={}", 
                            playerId, region);
                    
                    // Отправляем событие для обновления статуса игрока в MongoDB
                    eventPublisherService.publishPlayerStatusUpdateEvent(playerId, "AVAILABLE");
                    logger.info("Событие PlayerStatusUpdateEvent отправлено: playerId={}, status=AVAILABLE", 
                            playerId);
                } catch (Exception e) {
                    logger.error("Ошибка при отправке событий при отключении игрока: playerId={}, region={}", 
                            playerId, region, e);
                }
            } else {
                logger.info("WebSocket соединение закрыто: sessionId={}, playerId={}, status={}", 
                        sessionId, playerId, status);
            }
        } else {
            logger.info("WebSocket соединение закрыто: sessionId={}, status={}", sessionId, status);
        }
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

    private void sendFormattedMessage(WebSocketSession session, GameMessage message) {
        try {
            if (session.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
                logger.debug("Отформатированное сообщение отправлено: type={}, sessionId={}", 
                        message.getType(), session.getId());
            }
        } catch (Exception e) {
            logger.error("Ошибка при отправке отформатированного сообщения: sessionId={}", 
                    session.getId(), e);
        }
    }

    private void subscribeToGameUpdates(WebSocketSession session, String matchId) {
        String sessionId = session.getId();
        PlayerSessionInfo sessionInfo = sessionIdToInfo.get(sessionId);
        String playerId = sessionInfo != null ? sessionInfo.getPlayerId() : null;
        String gameUpdatesTopic = "game_updates_" + matchId;

        if (gameUpdatesSubscriptions.containsKey(matchId) && 
            gameUpdatesSubscriptions.get(matchId).containsKey(sessionId)) {
            logger.debug("Сессия уже подписана на game_updates: sessionId={}, matchId={}, playerId={}", 
                    sessionId, matchId, playerId);
            return;
        }

        MessageListener gameUpdatesListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String rawMessage = new String(message.getBody());
                    logger.info("Получено сообщение из game_updates: topic={}, message={}, sessionId={}, playerId={}", 
                            gameUpdatesTopic, rawMessage, sessionId, playerId);

                    if (session.isOpen()) {
                        GameMessage formattedMessage = messageFormatterService.formatMessage(rawMessage, gameUpdatesTopic);
                        sendFormattedMessage(session, formattedMessage);
                        logger.debug("Сообщение отправлено через WebSocket: sessionId={}, playerId={}, matchId={}", 
                                sessionId, playerId, matchId);
                    } else {
                        logger.warn("Сессия закрыта, удаляем подписку: sessionId={}, matchId={}, playerId={}", 
                                sessionId, matchId, playerId);
                        unsubscribeFromGameUpdates(sessionId, matchId);
                    }
                } catch (Exception e) {
                    logger.error("Ошибка при обработке сообщения из game_updates: topic={}, sessionId={}, playerId={}", 
                            gameUpdatesTopic, sessionId, playerId, e);
                }
            }
        };

        gameUpdatesSubscriptions.computeIfAbsent(matchId, k -> new ConcurrentHashMap<>())
                .put(sessionId, gameUpdatesListener);

        redisMessageListenerContainer.addMessageListener(gameUpdatesListener, new ChannelTopic(gameUpdatesTopic));
        
        int totalSubscriptions = gameUpdatesSubscriptions.get(matchId).size();
        logger.info("Подписка на game_updates создана: topic={}, sessionId={}, matchId={}, playerId={}, totalSubscriptions={}", 
                gameUpdatesTopic, sessionId, matchId, playerId, totalSubscriptions);
    }
    
    private void unsubscribeFromGameUpdates(String sessionId, String matchId) {
        Map<String, MessageListener> listeners = gameUpdatesSubscriptions.get(matchId);
        if (listeners != null) {
            MessageListener listener = listeners.remove(sessionId);
            if (listener != null) {
                String gameUpdatesTopic = "game_updates_" + matchId;
                redisMessageListenerContainer.removeMessageListener(listener, new ChannelTopic(gameUpdatesTopic));
                logger.info("Отписка от game_updates: sessionId={}, matchId={}", sessionId, matchId);
            }
            
            if (listeners.isEmpty()) {
                gameUpdatesSubscriptions.remove(matchId);
            }
        }
    }

    public void sendStatsAndCloseConnection(PlayerStatsUpdatedEvent event) {
        String playerId = event.playerId();
        
        PlayerSessionInfo sessionInfo = playerIdToInfo.get(playerId);
        
        if (sessionInfo == null) {
            logger.warn("Сессия не найдена для отправки статистики: playerId={}", playerId);
            return;
        }

        WebSocketSession session = sessionInfo.getSession();
        if (!session.isOpen()) {
            logger.warn("Сессия закрыта для отправки статистики: playerId={}", playerId);
            return;
        }

        try {
            Map<String, Object> statsData = new java.util.HashMap<>();
            statsData.put("rating", event.rating());
            statsData.put("winsCount", event.winsCount());
            statsData.put("lossesCount", event.lossesCount());

            GameMessage statsMessage = new GameMessage(
                    GameMessage.MessageType.GAME_RESULT,
                    "Статистика обновлена",
                    "Ваша статистика обновлена",
                    statsData
            );

            sendFormattedMessage(session, statsMessage);
            logger.info("Статистика отправлена игроку: playerId={}, rating={}, wins={}, losses={}", 
                    playerId, event.rating(), event.winsCount(), event.lossesCount());

            Thread.sleep(1000);

            if (session.isOpen()) {
                sessionIdToInfo.remove(session.getId());
                playerIdToInfo.remove(playerId);
                session.close(org.springframework.web.socket.CloseStatus.NORMAL.withReason("Game finished"));
                logger.info("WebSocket соединение закрыто после отправки статистики: playerId={}", playerId);
            }
        } catch (Exception e) {
            logger.error("Ошибка при отправке статистики и закрытии соединения: playerId={}", playerId, e);
        }
    }
}
