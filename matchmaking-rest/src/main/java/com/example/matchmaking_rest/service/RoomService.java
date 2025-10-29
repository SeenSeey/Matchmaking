package com.example.matchmaking_rest.service;

import com.example.matcmaking_api.dto.room.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

public interface RoomService {
    RoomResponse createRoom(CreateRoomRequest request);
    RoomResponse updateRoom(Long id, UpdateRoomRequest request);
    RoomResponse getRoomById(Long id);
    PagedModel<EntityModel<RoomShortResponse>> listRooms(String name, Integer maxPlayers, String roomMap, Boolean openOnly, int page, int size);
    JoinRoomResponse joinRoom(Long roomId, JoinRoomRequest request);
    void leaveRoom(Long roomId, Long playerId);
    PagedModel<EntityModel<RoomShortResponse>> recommendRooms(int page, int size);
}
