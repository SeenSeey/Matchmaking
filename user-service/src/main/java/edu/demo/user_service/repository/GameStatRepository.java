package edu.demo.user_service.repository;

import edu.demo.user_service.model.GameStat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameStatRepository extends MongoRepository<GameStat, String> {
}
