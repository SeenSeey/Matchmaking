package com.example.matchmaking_rest;

import com.example.matchmaking_rest.model.Player;
import com.example.matchmaking_rest.model.Room;
import com.example.matchmaking_rest.repository.InMemoryPlayerRepository;
import com.example.matchmaking_rest.repository.InMemoryRoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final InMemoryPlayerRepository playerRepository;
    private final InMemoryRoomRepository roomRepository;

    public DataInitializer(InMemoryPlayerRepository playerRepository, InMemoryRoomRepository roomRepository) {
        this.playerRepository = playerRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Player p1 = playerRepository.save(new Player(null, "Alice", "EU", 1500));
        Player p2 = playerRepository.save(new Player(null, "Bob", "US", 1700));
        Player p3 = playerRepository.save(new Player(null, "Charlie", "EU", 1400));
        Player p4 = playerRepository.save(new Player(null, "Diana", "ASIA", 1800));
        Player p5 = playerRepository.save(new Player(null, "Eve", "EU", 2000));

        Room r1 = new Room("Arena Alpha", "Map1", 4, "OPEN", 1L);
        r1.setPlayers(Set.of(p1.getId(), p2.getId()));
        roomRepository.save(r1);

        Room r2 = new Room("Desert Battle", "Map2", 3, "OPEN", 2L);
        r2.setPlayers(Set.of(p3.getId()));
        roomRepository.save(r2);

        Room r3 = new Room("Frozen Valley", "Map3", 5, "CLOSED", 3L);
        r3.setPlayers(Set.of(p4.getId(), p5.getId()));
        roomRepository.save(r3);

        Room r4 = new Room("Training Grounds", "Map1", 2, "OPEN", 4L);
        roomRepository.save(r4);

        Room r5 = new Room("Night City", "Map4", 6, "OPEN", 5L);
        r5.setPlayers(Set.of(p1.getId(), p3.getId(), p5.getId()));
        roomRepository.save(r5);

        System.out.println("Test data initialized: "
                + playerRepository.findAll().size() + " players, "
                + roomRepository.findAll().size() + " rooms.");
    }
}
