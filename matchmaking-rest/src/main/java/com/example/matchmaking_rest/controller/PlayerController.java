package com.example.matchmaking_rest.controller;

import com.example.matchmaking_rest.assembler.PlayerModelAssembler;
import com.example.matchmaking_rest.service.PlayerService;
import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.example.matcmaking_api.endpoints.PlayerApi;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController implements PlayerApi {
    private final PlayerService playerService;
    private final PlayerModelAssembler playerModelAssembler;

    public PlayerController(PlayerService playerService, PlayerModelAssembler playerModelAssembler) {
        this.playerService = playerService;
        this.playerModelAssembler = playerModelAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<PlayerResponse>> registerPlayer(PlayerRequest request) {
        PlayerResponse created = playerService.create(request);
        EntityModel<PlayerResponse> entityModel = playerModelAssembler.toModel(created);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<PlayerResponse> getPlayerById(Long playerId) {
        PlayerResponse player = playerService.getById(playerId);
        return playerModelAssembler.toModel(player);
    }
}

