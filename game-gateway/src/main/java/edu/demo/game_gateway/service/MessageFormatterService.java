package edu.demo.game_gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.game_gateway.dto.GameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageFormatterService {
    private static final Logger logger = LoggerFactory.getLogger(MessageFormatterService.class);
    
    private final ObjectMapper objectMapper;

    public MessageFormatterService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public GameMessage formatMessage(String rawMessage, String channel) {
        try {
            if (rawMessage.trim().startsWith("{")) {
                return formatJsonMessage(rawMessage, channel);
            } else {
                return formatTextMessage(rawMessage, channel);
            }
        } catch (Exception e) {
            logger.error("Ошибка при форматировании сообщения: message={}, channel={}", 
                    rawMessage, channel, e);
            return new GameMessage(
                    GameMessage.MessageType.CONNECTION,
                    "Ошибка",
                    "Не удалось обработать сообщение: " + rawMessage
            );
        }
    }

    private GameMessage formatJsonMessage(String jsonMessage, String channel) throws Exception {
        JsonNode root = objectMapper.readTree(jsonMessage);

        if (root.has("type") && root.get("type").asText().equals("GAME_READY")) {
            return formatGameReadyMessage();
        } else if (root.has("result")) {
            return formatGameResultMessage(root);
        } else if (root.has("tick") && root.has("players")) {
            return formatGameUpdateMessage(root);
        } else {
            return new GameMessage(
                    GameMessage.MessageType.CONNECTION,
                    "Уведомление",
                    jsonMessage
            );
        }
    }

    private GameMessage formatTextMessage(String textMessage, String channel) {
        if (textMessage.contains("Соединение установлено")) {
            return new GameMessage(
                    GameMessage.MessageType.CONNECTION,
                    "Подключение",
                    "Вы успешно подключены к игровому серверу"
            );
        } else if (textMessage.contains("Поиск соперника")) {
            return new GameMessage(
                    GameMessage.MessageType.SEARCHING,
                    "Поиск соперника",
                    "Ищем для вас достойного противника..."
            );
        } else if (textMessage.contains("Пара найдена") || textMessage.contains("Match ID:")) {
            String matchId = extractMatchId(textMessage);
            Map<String, String> data = new HashMap<>();
            if (matchId != null) {
                data.put("matchId", matchId);
            }
            return new GameMessage(
                    GameMessage.MessageType.MATCH_FOUND,
                    "Соперник найден!",
                    "Отличный противник найден! Подготовьтесь к битве!",
                    data
            );
        } else if (textMessage.contains("Game Over")) {
            return new GameMessage(
                    GameMessage.MessageType.GAME_OVER,
                    "Игра завершена",
                    "Время вышло! Подсчитываем результаты..."
            );
        } else {
            return new GameMessage(
                    GameMessage.MessageType.CONNECTION,
                    "Уведомление",
                    textMessage
            );
        }
    }

    private GameMessage formatGameReadyMessage() {
        return new GameMessage(
                GameMessage.MessageType.GAME_READY,
                "Игра готова",
                "Оба игрока готовы! Битва начинается через несколько секунд..."
        );
    }

    private GameMessage formatGameResultMessage(JsonNode root) {
        String result = root.get("result").asText();
        int damageDealt = root.has("damageDealt") ? root.get("damageDealt").asInt() : 0;
        int damageReceived = root.has("damageReceived") ? root.get("damageReceived").asInt() : 0;

        Map<String, Object> data = new HashMap<>();
        data.put("damageDealt", damageDealt);
        data.put("damageReceived", damageReceived);

        String title;
        String content;

        if (damageDealt == damageReceived) {
            title = "Ничья";
            content = String.format("Игра завершилась ничьей!\nНанесено урона: %d\nПолучено урона: %d", 
                    damageDealt, damageReceived);

        } else if (result.equals("You Won") || damageDealt > damageReceived) {
            title = "Победа!";
            content = String.format("Поздравляем! Вы одержали победу!\nНанесено урона: %d\nПолучено урона: %d", 
                    damageDealt, damageReceived);

        } else {
            title = "Поражение";
            content = String.format("К сожалению, вы проиграли.\nНанесено урона: %d\nПолучено урона: %d", 
                    damageDealt, damageReceived);
        }

        return new GameMessage(
                GameMessage.MessageType.GAME_RESULT,
                title,
                content,
                data
        );
    }

    private GameMessage formatGameUpdateMessage(JsonNode root) {
        int tick = root.get("tick").asInt();
        JsonNode playersNode = root.get("players");

        Map<String, Object> gameData = new HashMap<>();
        gameData.put("tick", tick);
        gameData.put("timeRemaining", 30 - tick);

        Map<String, Integer> playersDamage = new HashMap<>();
        if (playersNode.isArray()) {
            for (JsonNode player : playersNode) {
                String playerId = player.get("playerId").asText();
                int damage = player.get("damage").asInt();
                playersDamage.put(playerId, damage);
            }
        }
        gameData.put("players", playersDamage);

        String content = String.format("Ход %d/30\n", tick);
        if (playersNode.isArray() && playersNode.size() >= 2) {
            int player1Damage = playersNode.get(0).get("damage").asInt();
            int player2Damage = playersNode.get(1).get("damage").asInt();
            content += String.format("Игрок 1: %d урона\nИгрок 2: %d урона", 
                    player1Damage, player2Damage);
        }

        return new GameMessage(
                GameMessage.MessageType.GAME_UPDATE,
                "Обновление боя",
                content,
                gameData
        );
    }

    private String extractMatchId(String message) {
        if (message.contains("Match ID:")) {
            String[] parts = message.split("Match ID:");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return null;
    }

    public GameMessage createConnectionMessage() {
        return new GameMessage(
                GameMessage.MessageType.CONNECTION,
                "Подключение",
                "Вы успешно подключены к игровому серверу"
        );
    }

    public GameMessage createSearchingMessage() {
        return new GameMessage(
                GameMessage.MessageType.SEARCHING,
                "Поиск соперника",
                "Ищем для вас достойного противника..."
        );
    }
}
