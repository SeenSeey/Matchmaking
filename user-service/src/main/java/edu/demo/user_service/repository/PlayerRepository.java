package edu.demo.user_service.repository;

import edu.demo.user_service.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {
    Optional<Player> findById(String id);
}
