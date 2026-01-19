package edu.demo.matchmaking_gateway.controller;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.GameResponse;
import com.example.matcmaking_api.endpoints.GameApi;
import edu.demo.matchmaking_gateway.assembler.GameModelAssembler;
import edu.demo.matchmaking_gateway.service.GameService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController implements GameApi {
    private final GameService gameService;
    private final GameModelAssembler gameModelAssembler;

    public GameController(GameService gameService, GameModelAssembler gameModelAssembler) {
        this.gameService = gameService;
        this.gameModelAssembler = gameModelAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<GameResponse>> startGame(@Valid GameRequest request) {
        GameResponse created = gameService.startGame(request);
        EntityModel<GameResponse> entityModel = gameModelAssembler.toModel(created);

        return ResponseEntity
                .ok()
                .body(entityModel);
    }

    @Override
    public ResponseEntity<Void> leaveGame(@Valid GameRequest request) {
        gameService.leaveGame(request);
        return ResponseEntity.noContent().build();
    }
}