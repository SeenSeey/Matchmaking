package com.example.matchmaking_rest.repository;

import com.example.matchmaking_rest.model.Player;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryPlayerRepository {
    private final Map<Long, Player> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Player save(Player p) {
        if (p.getId() == null) p.setId(seq.getAndIncrement());
        System.out.println("=== PlayerId " + p.getId());
        storage.put(p.getId(), p);
        return p;
    }

    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Collection<Player> findAll() { return storage.values(); }
}
