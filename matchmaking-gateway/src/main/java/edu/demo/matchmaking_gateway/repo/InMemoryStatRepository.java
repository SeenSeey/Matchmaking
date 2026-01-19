package edu.demo.matchmaking_gateway.repo;

import edu.demo.matchmaking_gateway.model.StatAllTime;
import edu.demo.matchmaking_gateway.model.StatGame;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryStatRepository {
    private final Map<Long, StatGame> gameStatsStorage = new ConcurrentHashMap<>();
    private final Map<Long, StatAllTime> allTimeStatsStorage = new ConcurrentHashMap<>();
    private final AtomicLong gameStatSeq = new AtomicLong(1);
    private final AtomicLong allTimeStatSeq = new AtomicLong(1);

    public StatGame saveGameStat(StatGame statGame) {
        if (statGame.getId() == null) statGame.setId(gameStatSeq.getAndIncrement());
        System.out.println("=== StatGameId " + statGame.getId());
        gameStatsStorage.put(statGame.getId(), statGame);
        return statGame;
    }

    public Optional<StatGame> findLastGameStatByPlayerId(String playerId) {
        return gameStatsStorage.values().stream()
                .filter(stat -> stat.getPlayerId().equals(playerId))
                .max(Comparator.comparing(StatGame::getId));
    }

    public StatAllTime saveAllTimeStat(StatAllTime statAllTime) {
        if (statAllTime.getId() == null) statAllTime.setId(allTimeStatSeq.getAndIncrement());
        System.out.println("=== StatAllTimeId " + statAllTime.getId());
        allTimeStatsStorage.put(statAllTime.getId(), statAllTime);
        return statAllTime;
    }

    public Optional<StatAllTime> findAllTimeStatByPlayerId(String playerId) {
        return allTimeStatsStorage.values().stream()
                .filter(stat -> stat.getPlayerId().equals(playerId))
                .findFirst();
    }

    public List<StatAllTime> findAllTimeStatsSortedByWins(int page, int size) {
        return allTimeStatsStorage.values().stream()
                .sorted(Comparator.comparing(StatAllTime::getWinsAmount).reversed()
                        .thenComparing(StatAllTime::getPlayerId))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public long countAllTimeStats() {
        return allTimeStatsStorage.size();
    }
}
