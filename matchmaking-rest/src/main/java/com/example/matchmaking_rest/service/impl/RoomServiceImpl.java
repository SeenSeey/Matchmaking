package com.example.matchmaking_rest.service.impl;

import com.example.events_contract.PlayerJoinedRoomEvent;
import com.example.events_contract.PlayerLeftRoomEvent;
import com.example.events_contract.RoomCreatedEvent;
import com.example.events_contract.RoomUpdatedEvent;
import com.example.matchmaking_rest.config.RabbitMQConfig;
import com.example.matchmaking_rest.model.Player;
import com.example.matchmaking_rest.model.Room;
import com.example.matchmaking_rest.repository.InMemoryPlayerRepository;
import com.example.matchmaking_rest.repository.InMemoryRoomRepository;
import com.example.matchmaking_rest.service.RoomService;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.example.matcmaking_api.dto.room.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {
    private final InMemoryRoomRepository roomRepository;
    private final InMemoryPlayerRepository playerRepository;
    private final RabbitTemplate rabbitTemplate;

    public RoomServiceImpl(InMemoryRoomRepository roomRepository, InMemoryPlayerRepository playerRepository, RabbitTemplate rabbitTemplate) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        Room room = new Room(
                request.roomName(),
                request.roomMap(),
                request.maxPlayers(),
                request.roomStatus(),
                request.ownerId()
        );

        Set<Long> players = new HashSet<>();
        if (request.ownerId() != null) {
            players.add(request.ownerId());
        }
        room.setPlayers(players);

        Room saved = roomRepository.save(room);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ROOM_CREATED,
                toRoomCreatedEvent(saved)
        );

        return toRoomResponse(saved);
    }

    @Override
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));

        room.setName(request.roomName());
        room.setRoomMap(request.roomMap());
        if (request.maxPlayers() >= room.getPlayers().size()) {
            room.setMaxPlayers(request.maxPlayers());
        }

        roomRepository.save(room);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_ROOM_UPDATED,
                toRoomUpdatedEvent(room)
        );

        return toRoomResponse(room);
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));
        return toRoomResponse(room);
    }

    @Override
    public PagedModel<EntityModel<RoomShortResponse>> listRooms(
            String name, Integer maxPlayers, String roomMap, Boolean openOnly, int page, int size
    ) {
        List<Room> all = roomRepository.search(name, maxPlayers, roomMap, openOnly);

        List<RoomShortResponse> content = paginate(all, page, size)
                .stream().map(this::toRoomShortResponse).toList();

        List<EntityModel<RoomShortResponse>> wrapped = content.stream()
                .map(EntityModel::of).toList();

        return PagedModel.of(wrapped, new PagedModel.PageMetadata(size, page, all.size()));
    }

    @Override
    public JoinRoomResponse joinRoom(Long roomId, JoinRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        Player player = playerRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + request.userId()));

        if (!"OPEN".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Room is closed");
        }
        if (room.getPlayers().contains(player.getId())) {
            throw new IllegalStateException("Player already in room");
        }
        if (room.getPlayers().size() >= room.getMaxPlayers()) {
            throw new IllegalStateException("Room is full");
        }

        Set<Long> newPlayers = new HashSet<>(room.getPlayers());
        newPlayers.add(player.getId());
        room.setPlayers(newPlayers);

        if (newPlayers.size() >= room.getMaxPlayers()) {
            room.setStatus("CLOSED");
        }

        roomRepository.save(room);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_PLAYER_JOINED,
                new PlayerJoinedRoomEvent(
                        player.getId(),
                        room.getId(),
                        player.getUsername(),
                        player.getRegion(),
                        player.getRating(),
                        room.getPlayers().size()
                )
        );

        return new JoinRoomResponse(room.getId(), toPlayerResponse(player));
    }

    @Override
    public void leaveRoom(Long roomId, Long playerId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        if (!room.getPlayers().contains(playerId)) return;

        Set<Long> newPlayers = new HashSet<>(room.getPlayers());
        newPlayers.remove(playerId);
        room.setPlayers(newPlayers);

        if ("CLOSED".equalsIgnoreCase(room.getStatus()) && newPlayers.size() < room.getMaxPlayers()) {
            room.setStatus("OPEN");
        }

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_PLAYER_LEFT,
                new PlayerLeftRoomEvent(playerId, room.getId(), room.getPlayers().size())
        );

        roomRepository.save(room);
    }

    @Override
    public PagedModel<EntityModel<RoomShortResponse>> recommendRooms(int page, int size) {
        List<Room> openRooms = roomRepository.findAll().stream()
                .filter(r -> "OPEN".equalsIgnoreCase(r.getStatus()))
                .sorted(Comparator.comparingDouble(this::avgRating).reversed())
                .toList();

        List<RoomShortResponse> content = paginate(openRooms, page, size)
                .stream().map(this::toRoomShortResponse).toList();

        List<EntityModel<RoomShortResponse>> wrapped = content.stream()
                .map(EntityModel::of).toList();

        return PagedModel.of(wrapped, new PagedModel.PageMetadata(size, page, openRooms.size()));
    }

    private List<Room> paginate(List<Room> list, int page, int size) {
        int from = page * size;
        int to = Math.min(from + size, list.size());
        if (from > list.size()) return List.of();
        return list.subList(from, to);
    }

    private double avgRating(Room room) {
        if (room.getPlayers().isEmpty()) return 0;
        return room.getPlayers().stream()
                .map(playerRepository::findById)
                .flatMap(Optional::stream)
                .mapToInt(Player::getRating)
                .average()
                .orElse(0);
    }

    private RoomResponse toRoomResponse(Room room) {
        Long ownerId = room.getPlayers().stream().findFirst().orElse(null);
        PlayerResponse owner = null;
        if (ownerId != null) {
            owner = playerRepository.findById(ownerId)
                    .map(this::toPlayerResponse)
                    .orElse(null);
        }

        List<PlayerResponse> players = room.getPlayers().stream()
                .map(playerRepository::findById)
                .flatMap(Optional::stream)
                .map(this::toPlayerResponse)
                .toList();

        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getRoomMap(),
                owner,
                players.size(),
                room.getMaxPlayers(),
                avgRating(room),
                room.getStatus(),
                players,
                room.getCreatedAt()
        );
    }

    private RoomShortResponse toRoomShortResponse(Room room) {
        Long ownerId = room.getPlayers().stream().findFirst().orElse(null);
        List<Long> players = new ArrayList<>(room.getPlayers());

        return new RoomShortResponse(
                room.getId(),
                room.getName(),
                room.getRoomMap(),
                ownerId,
                players.size(),
                room.getMaxPlayers(),
                avgRating(room),
                room.getStatus(),
                room.getCreatedAt()
        );
    }

    private PlayerResponse toPlayerResponse(Player p) {
        return new PlayerResponse(p.getId(), p.getUsername(), p.getRating(), p.getRegion());
    }

    private RoomCreatedEvent toRoomCreatedEvent(Room room) {
        return new RoomCreatedEvent(
                room.getId(),
                room.getName(),
                room.getRoomMap(),
                room.getMaxPlayers(),
                room.getStatus(),
                room.getCreatedAt()
        );
    }

    private RoomUpdatedEvent toRoomUpdatedEvent(Room room) {
        return new RoomUpdatedEvent(
                room.getId(),
                room.getName(),
                room.getRoomMap(),
                room.getMaxPlayers(),
                room.getStatus(),
                room.getCreatedAt()
        );
    }

}
