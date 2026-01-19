package edu.demo.matchmaking_gateway.repo;

import edu.demo.matchmaking_gateway.model.Player;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlayerRepository {
    private final Map<String, Player> storage = new ConcurrentHashMap<>();

    public Player save(Player player) {
        System.out.println("=== PlayerId " + player.getId());
        storage.put(player.getId(), player);
        return player;
    }

    public Optional<Player> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Player> findAll() {
        return storage.values().stream().toList();
    }
}
