package edu.demo.matchmaker_service.service;

import edu.demo.matchmaker_service.dto.PlayerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchmakingService {
    private static final Logger logger = LoggerFactory.getLogger(MatchmakingService.class);

    private static final String POOL_KEY_PREFIX = "matchmaking:pool:";
    private static final String MODE = "ranked";
    private static final int RATING_DELTA = 150;
    
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> findPairScript;

    private final ConcurrentHashMap<String, PlayerInfo> playerInfoCache = new ConcurrentHashMap<>();

    public MatchmakingService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        
        try {
            ClassPathResource scriptResource = new ClassPathResource("lua/find-and-remove-pair.lua");
            if (!scriptResource.exists()) {
                throw new IllegalStateException("Lua script not found: lua/find-and-remove-pair.lua");
            }
            String script = new String(scriptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            this.findPairScript = new DefaultRedisScript<>();
            this.findPairScript.setScriptText(script);
            this.findPairScript.setResultType(List.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load Lua script", e);
        }
    }

    public String[] findAndRemovePair(String playerId, int rating, String region) {
        String poolKey = POOL_KEY_PREFIX + region + ":" + MODE;

        @SuppressWarnings("unchecked")
        List<String> result = redisTemplate.execute(
                findPairScript,
                Collections.singletonList(poolKey),
                playerId,
                String.valueOf(rating),
                String.valueOf(RATING_DELTA)
        );

        if (result != null && result.size() == 2) {
            return new String[]{result.get(0), result.get(1)};
        }

        return null;
    }

    public void removePlayerFromQueue(String playerId, String region) {
        String poolKey = POOL_KEY_PREFIX + region + ":" + MODE;
        Long removed = redisTemplate.opsForZSet().remove(poolKey, playerId);
        if (removed != null && removed > 0) {
            logger.info("Игрок удален из очереди ожидания: playerId={}, region={}, poolKey={}", 
                    playerId, region, poolKey);
        } else {
            logger.debug("Игрок не найден в очереди ожидания: playerId={}, region={}, poolKey={}", 
                    playerId, region, poolKey);
        }
    }
}
