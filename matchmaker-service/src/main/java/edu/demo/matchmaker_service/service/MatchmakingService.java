package edu.demo.matchmaker_service.service;

import edu.demo.matchmaker_service.dto.PlayerInfo;
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

    public void cachePlayerInfo(String playerId, String nickname, int rating, String region) {
        playerInfoCache.put(playerId, new PlayerInfo(playerId, nickname, rating, region));
    }

    public PlayerInfo[] findAndRemovePair(String playerId, int rating, String region) {
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
            String player1Id = result.get(0);
            String player2Id = result.get(1);

            PlayerInfo player1 = playerInfoCache.get(player1Id);
            PlayerInfo player2 = playerInfoCache.get(player2Id);

            playerInfoCache.remove(player1Id);
            playerInfoCache.remove(player2Id);
            
            if (player1 != null && player2 != null) {
                return new PlayerInfo[]{player1, player2};
            } else {
                if (player1 == null) {
                    player1 = new PlayerInfo(player1Id, "Unknown", rating, region);
                }
                if (player2 == null) {
                    player2 = new PlayerInfo(player2Id, "Unknown", rating, region);
                }
                return new PlayerInfo[]{player1, player2};
            }
        }
        
        return null;
    }
}
