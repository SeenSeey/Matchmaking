package edu.demo.game_service.service;

import edu.demo.game_service.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.events_contract.GameFinishedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.demo.game_service.config.RabbitMQConfig;
import edu.demo.game_service.dto.GameResultMessage;
import edu.demo.game_service.dto.GameStatusMessage;
import edu.demo.game_service.dto.GameUpdateMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final int GAME_DURATION_SECONDS = 30;
    private static final int TICK_INTERVAL_SECONDS = 1;
    private static final int MAX_DAMAGE_PER_TICK = 100;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public GameService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void startGame(String matchId, String player1Id, String player2Id, String region) {
        logger.info("Создание игры: matchId={}, player1Id={}, player2Id={}, region={}", 
                matchId, player1Id, player2Id, region);

        Game game = new Game(matchId, player1Id, player2Id, region);
        activeGames.put(matchId, game);

        sendGameReadyStatus(player1Id, player2Id);

        scheduler.schedule(() -> {
            startGameLoop(game);
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private void sendGameReadyStatus(String player1Id, String player2Id) {
        try {
            GameStatusMessage statusMessage = new GameStatusMessage(edu.demo.game_service.dto.GameStatus.GAME_READY);
            String messageJson = objectMapper.writeValueAsString(statusMessage);

            String channel1 = "user_notifications_" + player1Id;
            String channel2 = "user_notifications_" + player2Id;

            redisTemplate.convertAndSend(channel1, messageJson);
            redisTemplate.convertAndSend(channel2, messageJson);

            logger.info("Статус GAME_READY отправлен игрокам: player1Id={}, player2Id={}", 
                    player1Id, player2Id);
        } catch (Exception e) {
            logger.error("Ошибка при отправке статуса GAME_READY: player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
        }
    }

    public void startGameLoop(Game game) {
        logger.info("Запуск игрового цикла: matchId={}", game.getMatchId());

        final int[] tick = {0};

        Runnable gameTick = new Runnable() {
            @Override
            public void run() {
                if (!game.isRunning() || tick[0] >= GAME_DURATION_SECONDS) {
                    game.stop();
                    activeGames.remove(game.getMatchId());
                    logger.info("Игра завершена: matchId={}, finalDamage: player1={}, player2={}", 
                            game.getMatchId(), game.getPlayer1Damage(), game.getPlayer2Damage());
                    return;
                }

                tick[0]++;

                int player1Damage = (int) (Math.random() * (MAX_DAMAGE_PER_TICK + 1));
                int player2Damage = (int) (Math.random() * (MAX_DAMAGE_PER_TICK + 1));

                game.addPlayer1Damage(player1Damage);
                game.addPlayer2Damage(player2Damage);

                sendGameUpdate(game, tick[0]);
            }
        };

        ScheduledFuture<?> tickFuture = scheduler.scheduleAtFixedRate(
                gameTick, 0, TICK_INTERVAL_SECONDS, TimeUnit.SECONDS);

        scheduler.schedule(() -> {
            tickFuture.cancel(false);
            game.stop();
            finishGame(game);
        }, GAME_DURATION_SECONDS, TimeUnit.SECONDS);
    }

    private void finishGame(Game game) {
        int player1Damage = game.getPlayer1Damage();
        int player2Damage = game.getPlayer2Damage();
        
        logger.info("Игра завершена: matchId={}, finalDamage: player1={}, player2={}", 
                game.getMatchId(), player1Damage, player2Damage);

        sendGameOverMessage(game.getPlayer1Id(), game.getPlayer2Id());

        String winnerId;
        
        if (player1Damage > player2Damage) {
            winnerId = game.getPlayer1Id();
        } else if (player2Damage > player1Damage) {
            winnerId = game.getPlayer2Id();
        } else {
            winnerId = game.getPlayer1Id();
        }

        if (player1Damage > player2Damage) {
            sendGameResult(game.getPlayer1Id(), game.getPlayer2Id(), game.getPlayer1Id(), 
                    player1Damage, player2Damage);
        } else if (player2Damage > player1Damage) {
            sendGameResult(game.getPlayer1Id(), game.getPlayer2Id(), game.getPlayer2Id(), 
                    player2Damage, player1Damage);
        } else {
            sendGameResultDraw(game.getPlayer1Id(), game.getPlayer2Id(), player1Damage, player2Damage);
        }

        GameFinishedEvent event = new GameFinishedEvent(
                game.getMatchId(),
                game.getPlayer1Id(),
                player1Damage,
                game.getPlayer2Id(),
                player2Damage,
                winnerId,
                game.getRegion()
        );
        
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.GAME_FINISHED_QUEUE, event);
            logger.info("Событие GameFinishedEvent отправлено в RabbitMQ: matchId={}, winnerId={}", 
                    game.getMatchId(), winnerId);
        } catch (Exception e) {
            logger.error("Ошибка при отправке GameFinishedEvent: matchId={}", game.getMatchId(), e);
            throw new RuntimeException("Не удалось отправить событие GameFinishedEvent", e);
        }

        activeGames.remove(game.getMatchId());
    }

    public void finishGameOnDisconnect(String matchId, String disconnectedPlayerId) {
        Game game = activeGames.get(matchId);
        
        if (game == null) {
            logger.warn("Игра не найдена для завершения при разрыве: matchId={}, disconnectedPlayerId={}", 
                    matchId, disconnectedPlayerId);
            return;
        }

        if (!game.isRunning()) {
            logger.debug("Игра уже завершена: matchId={}, disconnectedPlayerId={}", matchId, disconnectedPlayerId);
            return;
        }

        logger.warn("Завершение игры из-за разрыва соединения: matchId={}, disconnectedPlayerId={}", 
                matchId, disconnectedPlayerId);

        game.stop();

        int player1Damage = game.getPlayer1Damage();
        int player2Damage = game.getPlayer2Damage();

        String remainingPlayerId;
        String disconnectedId;
        int remainingPlayerDamage;
        int disconnectedPlayerDamage;

        if (game.getPlayer1Id().equals(disconnectedPlayerId)) {
            remainingPlayerId = game.getPlayer2Id();
            disconnectedId = game.getPlayer1Id();
            remainingPlayerDamage = player2Damage;
            disconnectedPlayerDamage = player1Damage;
        } else if (game.getPlayer2Id().equals(disconnectedPlayerId)) {
            remainingPlayerId = game.getPlayer1Id();
            disconnectedId = game.getPlayer2Id();
            remainingPlayerDamage = player1Damage;
            disconnectedPlayerDamage = player2Damage;
        } else {
            logger.error("Отключившийся игрок не найден в игре: matchId={}, disconnectedPlayerId={}, player1Id={}, player2Id={}", 
                    matchId, disconnectedPlayerId, game.getPlayer1Id(), game.getPlayer2Id());
            return;
        }

        logger.info("Игра завершена из-за разрыва соединения: matchId={}, winnerId={}, winnerDamage={}, loserId={}, loserDamage={}", 
                matchId, remainingPlayerId, remainingPlayerDamage, disconnectedId, disconnectedPlayerDamage);

        sendGameOverMessage(game.getPlayer1Id(), game.getPlayer2Id());

        sendGameResult(game.getPlayer1Id(), game.getPlayer2Id(), remainingPlayerId, 
                remainingPlayerDamage, disconnectedPlayerDamage);

        GameFinishedEvent event = new GameFinishedEvent(
                game.getMatchId(),
                game.getPlayer1Id(),
                player1Damage,
                game.getPlayer2Id(),
                player2Damage,
                remainingPlayerId,
                game.getRegion()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.GAME_FINISHED_QUEUE, event);
            logger.info("Событие GameFinishedEvent отправлено в RabbitMQ (разрыв соединения): matchId={}, winnerId={}", 
                    game.getMatchId(), remainingPlayerId);
        } catch (Exception e) {
            logger.error("Ошибка при отправке GameFinishedEvent (разрыв соединения): matchId={}", game.getMatchId(), e);
            throw new RuntimeException("Не удалось отправить событие GameFinishedEvent", e);
        }

        activeGames.remove(game.getMatchId());
    }

    private void sendGameOverMessage(String player1Id, String player2Id) {
        try {
            String message = "Game Over";
            String channel1 = "user_notifications_" + player1Id;
            String channel2 = "user_notifications_" + player2Id;

            redisTemplate.convertAndSend(channel1, message);
            redisTemplate.convertAndSend(channel2, message);

            logger.info("Сообщение 'Game Over' отправлено игрокам: player1Id={}, player2Id={}", 
                    player1Id, player2Id);
        } catch (Exception e) {
            logger.error("Ошибка при отправке 'Game Over': player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
        }
    }

    private void sendGameResult(String player1Id, String player2Id, String winnerId, 
                               int winnerDamage, int loserDamage) {
        try {
            GameResultMessage winnerMessage = new GameResultMessage(
                    "You Won",
                    winnerDamage,
                    loserDamage
            );
            String winnerJson = objectMapper.writeValueAsString(winnerMessage);
            redisTemplate.convertAndSend("user_notifications_" + winnerId, winnerJson);

            String loserId = winnerId.equals(player1Id) ? player2Id : player1Id;
            GameResultMessage loserMessage = new GameResultMessage(
                    "You Lose",
                    loserDamage,
                    winnerDamage
            );
            String loserJson = objectMapper.writeValueAsString(loserMessage);
            redisTemplate.convertAndSend("user_notifications_" + loserId, loserJson);

            logger.info("Результаты игры отправлены: winnerId={}, loserId={}, winnerDamage={}, loserDamage={}", 
                    winnerId, loserId, winnerDamage, loserDamage);
        } catch (Exception e) {
            logger.error("Ошибка при отправке результатов игры: player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
        }
    }

    private void sendGameResultDraw(String player1Id, String player2Id, int player1Damage, int player2Damage) {
        try {
            GameResultMessage message1 = new GameResultMessage(
                    "You Lose",
                    player1Damage,
                    player2Damage
            );
            GameResultMessage message2 = new GameResultMessage(
                    "You Lose",
                    player2Damage,
                    player1Damage
            );
            
            String json1 = objectMapper.writeValueAsString(message1);
            String json2 = objectMapper.writeValueAsString(message2);
            
            redisTemplate.convertAndSend("user_notifications_" + player1Id, json1);
            redisTemplate.convertAndSend("user_notifications_" + player2Id, json2);

            logger.info("Результаты ничьей отправлены: player1Id={}, player2Id={}, damage1={}, damage2={}", 
                    player1Id, player2Id, player1Damage, player2Damage);
        } catch (Exception e) {
            logger.error("Ошибка при отправке результатов ничьей: player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
        }
    }

    private void sendGameUpdate(Game game, int tick) {
        try {
            GameUpdateMessage.PlayerDamage[] players = new GameUpdateMessage.PlayerDamage[]{
                    new GameUpdateMessage.PlayerDamage(game.getPlayer1Id(), game.getPlayer1Damage()),
                    new GameUpdateMessage.PlayerDamage(game.getPlayer2Id(), game.getPlayer2Damage())
            };

            GameUpdateMessage updateMessage = new GameUpdateMessage(tick, players);
            String messageJson = objectMapper.writeValueAsString(updateMessage);

            String channel = "game_updates_" + game.getMatchId();
            redisTemplate.convertAndSend(channel, messageJson);

            logger.debug("Обновление игры отправлено: matchId={}, tick={}, player1Damage={}, player2Damage={}", 
                    game.getMatchId(), tick, game.getPlayer1Damage(), game.getPlayer2Damage());
        } catch (Exception e) {
            logger.error("Ошибка при отправке обновления игры: matchId={}, tick={}", 
                    game.getMatchId(), tick, e);
        }
    }
}
