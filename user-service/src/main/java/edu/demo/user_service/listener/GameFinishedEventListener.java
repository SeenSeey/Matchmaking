package edu.demo.user_service.listener;

import com.example.events_contract.GameFinishedEvent;
import com.example.events_contract.PlayerStatsUpdatedEvent;
import edu.demo.user_service.config.RabbitMQConfig;
import edu.demo.user_service.model.GameStat;
import edu.demo.user_service.model.Player;
import edu.demo.user_service.model.PlayerStatus;
import edu.demo.user_service.repository.GameStatRepository;
import edu.demo.user_service.repository.PlayerRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class GameFinishedEventListener {
    private static final Logger logger = LoggerFactory.getLogger(GameFinishedEventListener.class);

    private final PlayerRepository playerRepository;
    private final GameStatRepository gameStatRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, Boolean> processedEvents = new ConcurrentHashMap<>();

    public GameFinishedEventListener(PlayerRepository playerRepository, 
                                    GameStatRepository gameStatRepository,
                                    RabbitTemplate rabbitTemplate) {
        this.playerRepository = playerRepository;
        this.gameStatRepository = gameStatRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(
            queues = RabbitMQConfig.GAME_FINISHED_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleGameFinishedEvent(
            GameFinishedEvent event,
            Message message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String eventKey = event.matchId() + "_" + deliveryTag;

        if (processedEvents.containsKey(eventKey)) {
            logger.warn("Дубликат события игнорирован: eventKey={}, matchId={}", eventKey, event.matchId());
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
        logger.info("Получено событие GameFinishedEvent: matchId={}, player1Id={}, player1Damage={}, player2Id={}, player2Damage={}, winnerId={}", 
                event.matchId(), event.player1Id(), event.player1Damage(), 
                event.player2Id(), event.player2Damage(), event.winnerId());

        try {
            GameStat gameStat = new GameStat(
                    event.matchId(),
                    event.player1Id(),
                    event.player1Damage(),
                    event.player2Id(),
                    event.player2Damage(),
                    event.winnerId(),
                    event.region()
            );
            gameStatRepository.save(gameStat);
            logger.info("Статистика боя сохранена: matchId={}", event.matchId());

            Player player1 = playerRepository.findById(event.player1Id())
                    .orElseThrow(() -> new RuntimeException("Player not found: " + event.player1Id()));
            Player player2 = playerRepository.findById(event.player2Id())
                    .orElseThrow(() -> new RuntimeException("Player not found: " + event.player2Id()));

            int damageDifference = Math.abs(event.player1Damage() - event.player2Damage());
            String winnerId = event.winnerId();

            player1.setStatus(PlayerStatus.AVAILABLE);
            player2.setStatus(PlayerStatus.AVAILABLE);

            if (damageDifference > 0) {
                Player winner;
                Player loser;
                
                if (event.player1Id().equals(winnerId)) {
                    winner = player1;
                    loser = player2;
                } else {
                    winner = player2;
                    loser = player1;
                }

                int newWinnerRating = winner.getRating() + damageDifference;
                winner.setRating(newWinnerRating);

                int newLoserRating = Math.max(0, loser.getRating() - damageDifference);
                loser.setRating(newLoserRating);

                logger.info("Игроки обновлены: winnerId={}, newRating={}, loserId={}, newRating={}, damageDifference={}", 
                        winner.getId(), newWinnerRating, loser.getId(), newLoserRating, damageDifference);
            } else {
                logger.info("Ничья - рейтинг не изменен: player1Id={}, player2Id={}, damage1={}, damage2={}", 
                        event.player1Id(), event.player2Id(), event.player1Damage(), event.player2Damage());
            }

            playerRepository.save(player1);
            playerRepository.save(player2);

            sendPlayerStatsUpdatedEvent(player1);
            sendPlayerStatsUpdatedEvent(player2);

            channel.basicAck(deliveryTag, false);
            logger.debug("Событие успешно обработано: eventKey={}", eventKey);
        } catch (Exception e) {
            logger.error("Ошибка при обработке GameFinishedEvent: eventKey={}, matchId={}", 
                    eventKey, event.matchId(), e);
            processedEvents.remove(eventKey);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private void sendPlayerStatsUpdatedEvent(Player player) {
        try {
            long winsCount = gameStatRepository.findAll().stream()
                    .filter(stat -> stat.getWinnerId().equals(player.getId()))
                    .count();

            long lossesCount = gameStatRepository.findAll().stream()
                    .filter(stat -> (stat.getPlayer1Id().equals(player.getId()) || 
                                   stat.getPlayer2Id().equals(player.getId())) &&
                                   !stat.getWinnerId().equals(player.getId()))
                    .count();

            PlayerStatsUpdatedEvent event = new PlayerStatsUpdatedEvent(
                    player.getId(),
                    player.getRating(),
                    (int) winsCount,
                    (int) lossesCount
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, 
                    RabbitMQConfig.PLAYER_STATS_UPDATED_QUEUE, event);
            logger.info("Событие PlayerStatsUpdatedEvent отправлено: playerId={}, rating={}, wins={}, losses={}", 
                    player.getId(), player.getRating(), winsCount, lossesCount);
        } catch (Exception e) {
            logger.error("Ошибка при отправке PlayerStatsUpdatedEvent: playerId={}", player.getId(), e);
        }
    }
}
