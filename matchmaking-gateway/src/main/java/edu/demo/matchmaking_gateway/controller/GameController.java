package edu.demo.matchmaking_gateway.controller;

import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.StartGameResponse;
import com.example.matcmaking_api.endpoints.GameApi;
import edu.demo.matchmaking_gateway.assembler.StartGameModelAssembler;
import edu.demo.matchmaking_gateway.service.GameService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController implements GameApi {
    private final GameService gameService;
    private final StartGameModelAssembler startGameModelAssembler;

    public GameController(GameService gameService, StartGameModelAssembler startGameModelAssembler) {
        this.gameService = gameService;
        this.startGameModelAssembler = startGameModelAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<StartGameResponse>> startGame(@Valid GameRequest request) {
        StartGameResponse created = gameService.startGame(request);
        EntityModel<StartGameResponse> entityModel = startGameModelAssembler.toModel(created);

        return ResponseEntity
                .ok()
                .body(entityModel);
    }
}