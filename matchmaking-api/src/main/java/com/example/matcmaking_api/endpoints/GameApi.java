package com.example.matcmaking_api.endpoints;

import com.example.matcmaking_api.dto.StatusResponse;
import com.example.matcmaking_api.dto.game.GameRequest;
import com.example.matcmaking_api.dto.game.StartGameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "games", description = "API работы с играми")
@RequestMapping("/api/games")
public interface GameApi {

    @Operation(summary = "Начать игру")
    @ApiResponse(responseCode = "200", description = "Поиск игры начат")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "404", description = "Игрок не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Игрок уже в поиске или в игре", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping("/start")
    ResponseEntity<EntityModel<StartGameResponse>> startGame(@Valid @RequestBody GameRequest request);

}
