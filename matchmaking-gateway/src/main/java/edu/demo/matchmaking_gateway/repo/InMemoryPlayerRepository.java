package edu.demo.matchmaking_gateway.repo;

import edu.demo.matchmaking_gateway.model.Player;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPlayerRepository {
    private final Map<Long, Player> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Player save(Player player) {
        if (player.getId() == null) player.setId(seq.getAndIncrement());
        System.out.println("=== PlayerId " + player.getId());
        storage.put(player.getId(), player);
        return player;
    }

    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Player> findAll() {
        return storage.values().stream().toList();
    }
}
