package com.example.matchmaking_rest.graphql;

import com.example.matchmaking_rest.service.PlayerService;
import com.example.matchmaking_rest.service.RoomService;
import com.example.matcmaking_api.dto.room.CreateRoomRequest;
import com.example.matcmaking_api.dto.room.JoinRoomRequest;
import com.example.matcmaking_api.dto.room.RoomResponse;
import com.example.matcmaking_api.dto.room.UpdateRoomRequest;
import com.netflix.graphql.dgs.*;
import org.springframework.hateoas.EntityModel;

import java.util.Map;
import java.util.Objects;

@DgsComponent
public class RoomDataFetcher {
    private final RoomService roomService;
    private final PlayerService playerService;

    public RoomDataFetcher(RoomService roomService, PlayerService playerService) {
        this.roomService = roomService;
        this.playerService = playerService;
    }

    @DgsQuery
    public RoomResponse roomById(@InputArgument Long id) {
        return roomService.getRoomById(id);
    }

    @DgsQuery
    public Map<String, Object> rooms(
            @InputArgument String roomMap,
            @InputArgument String roomStatus,
            @InputArgument int page,
            @InputArgument int size
    ) {
        boolean openOnly = roomStatus != null && roomStatus.equalsIgnoreCase("OPEN");
        var paged = roomService.listRooms(null, null, roomMap, openOnly, page, size);
        var content = paged.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .toList();

        return Map.of(
                "content", content,
                "pageNumber", paged.getMetadata().getNumber(),
                "pageSize", paged.getMetadata().getSize(),
                "totalElements", paged.getMetadata().getTotalElements(),
                "totalPages", paged.getMetadata().getTotalPages(),
                "last", paged.getMetadata().getNumber() >= paged.getMetadata().getTotalPages() - 1
        );
    }

    @DgsMutation
    public RoomResponse createRoom(@InputArgument("input") Map<String, Object> input) {
        CreateRoomRequest request = new CreateRoomRequest(
                (String) input.get("roomName"),
                (String) input.get("roomMap"),
                (String) input.get("roomStatus"),
                ((Number) input.get("maxPlayers")).intValue(),
                Long.parseLong((String) input.get("ownerId"))
        );
        return roomService.createRoom(request);
    }

    @DgsMutation
    public RoomResponse updateRoom(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        UpdateRoomRequest request = new UpdateRoomRequest(
                (String) input.get("roomName"),
                (String) input.get("roomMap"),
                ((Number) input.get("maxPlayers")).intValue()
        );
        return roomService.updateRoom(id, request);
    }

    @DgsMutation
    public RoomResponse joinRoom(@InputArgument("input") Map<String, Object> input) {
        Long roomId = Long.parseLong(input.get("roomId").toString());
        Long userId = Long.parseLong(input.get("userId").toString());
        JoinRoomRequest request = new JoinRoomRequest(userId);
        roomService.joinRoom(roomId, request);
        return roomService.getRoomById(roomId);
    }

    @DgsMutation
    public RoomResponse leaveRoom(@InputArgument("input") Map<String, Object> input) {
        Long roomId = Long.parseLong(input.get("roomId").toString());
        Long userId = Long.parseLong(input.get("userId").toString());
        roomService.leaveRoom(roomId, userId);
        return roomService.getRoomById(roomId);
    }
}

