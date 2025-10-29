package com.example.matchmaking_rest.controller;

import com.example.matchmaking_rest.assembler.RoomModelAssembler;
import com.example.matchmaking_rest.assembler.RoomShortModelAssembler;
import com.example.matchmaking_rest.service.RoomService;
import com.example.matcmaking_api.dto.room.*;
import com.example.matcmaking_api.endpoints.RoomApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController implements RoomApi {
    private final RoomService roomService;
    private final RoomModelAssembler roomModelAssembler;
    private final RoomShortModelAssembler roomShortModelAssembler;
    private final PagedResourcesAssembler<RoomShortResponse> pagedResourcesAssembler;

    public RoomController(RoomService roomService,
                          RoomModelAssembler roomModelAssembler,
                          RoomShortModelAssembler roomShortModelAssembler,
                          PagedResourcesAssembler<RoomShortResponse> pagedResourcesAssembler) {
        this.roomService = roomService;
        this.roomModelAssembler = roomModelAssembler;
        this.roomShortModelAssembler = roomShortModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<RoomResponse>> createRoom(CreateRoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        EntityModel<RoomResponse> entityModel = roomModelAssembler.toModel(response);
        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<RoomResponse> getRoomById(Long id) {
        RoomResponse response = roomService.getRoomById(id);
        return roomModelAssembler.toModel(response);
    }

    @Override
    public EntityModel<RoomResponse> updateRoom(Long id, UpdateRoomRequest request) {
        RoomResponse response = roomService.updateRoom(id, request);
        return roomModelAssembler.toModel(response);
    }

    @Override
    public EntityModel<JoinRoomResponse> joinRoom(Long id, JoinRoomRequest request) {
        JoinRoomResponse response = roomService.joinRoom(id, request);
        EntityModel<JoinRoomResponse> model = EntityModel.of(response);
        model.add(Link.of("/api/rooms/" + id).withRel("room"));
        return model;
    }

    @Override
    public void leaveRoom(Long id, LeaveRoomRequest request) {
        roomService.leaveRoom(id, request.userId());
    }

    @Override
    public PagedModel<EntityModel<RoomShortResponse>> searchRooms(Integer maxPlayers, String roomMap, Boolean openOnly, int page, int size) {
        List<RoomShortResponse> rooms = roomService.listRooms(null, maxPlayers, roomMap, openOnly, page, size)
                .getContent()
                .stream()
                .map(EntityModel::getContent)
                .toList();

        Page<RoomShortResponse> pageData = new PageImpl<>(rooms, PageRequest.of(page, size), rooms.size());
        return pagedResourcesAssembler.toModel(pageData, roomShortModelAssembler);
    }

    @Override
    public PagedModel<EntityModel<RoomShortResponse>> recommendRooms(int page, int size) {
        List<RoomShortResponse> rooms = roomService.recommendRooms(page, size)
                .getContent()
                .stream()
                .map(EntityModel::getContent)
                .toList();

        Page<RoomShortResponse> pageData = new PageImpl<>(rooms, PageRequest.of(page, size), rooms.size());
        return pagedResourcesAssembler.toModel(pageData, roomShortModelAssembler);
    }
}
