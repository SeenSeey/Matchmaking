package edu.demo.matchmaking_gateway.repo;

import edu.demo.matchmaking_gateway.model.Game;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryGameRepository {
    private final Map<Long, Game> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Game save(Game game) {
        if (game.getId() == null) game.setId(seq.getAndIncrement());
        System.out.println("=== GameId " + game.getId());
        storage.put(game.getId(), game);
        return game;
    }

    public Optional<Game> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Game> findByPlayerId(String playerId) {
        return storage.values().stream()
                .filter(game -> game.getFirstPlayerId().equals(playerId) || game.getSecondPlayerId().equals(playerId))
                .findFirst();
    }
}
