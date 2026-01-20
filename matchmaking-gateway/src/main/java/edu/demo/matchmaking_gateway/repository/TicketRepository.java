package edu.demo.matchmaking_gateway.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class TicketRepository {
    private static final String TICKET_PREFIX = "ticket:";
    
    private final StringRedisTemplate redisTemplate;

    public TicketRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String ticketId, String jsonData, int expirationSeconds) {
        String key = TICKET_PREFIX + ticketId;
        redisTemplate.opsForValue().set(key, jsonData, expirationSeconds, TimeUnit.SECONDS);
    }

    public String findByTicketId(String ticketId) {
        String key = TICKET_PREFIX + ticketId;
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String ticketId) {
        String key = TICKET_PREFIX + ticketId;
        redisTemplate.delete(key);
    }
}
