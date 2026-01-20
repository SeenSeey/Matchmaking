package edu.demo.matchmaker_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(RedisNotificationService.class);
    
    private final StringRedisTemplate redisTemplate;

    public RedisNotificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void notifyPlayers(String player1Id, String player2Id, String matchId) {
        try {
            String message = "Пара найдена! Match ID: " + matchId;

            String channel1 = "user_notifications_" + player1Id;
            String channel2 = "user_notifications_" + player2Id;
            
            redisTemplate.convertAndSend(channel1, message);
            redisTemplate.convertAndSend(channel2, message);
            
            logger.info("Уведомления отправлены игрокам: player1Id={}, player2Id={}, matchId={}", 
                    player1Id, player2Id, matchId);
        } catch (Exception e) {
            logger.error("Ошибка при отправке уведомлений игрокам: player1Id={}, player2Id={}", 
                    player1Id, player2Id, e);
        }
    }
}
