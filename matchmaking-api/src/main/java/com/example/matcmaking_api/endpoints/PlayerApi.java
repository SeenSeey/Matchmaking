package com.example.matcmaking_api.endpoints;

import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import com.example.matcmaking_api.dto.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "players", description = "API работы с игроками")
@RequestMapping("/api/players")
public interface PlayerApi {

    @Operation(summary = "создание нового игрока")
    @ApiResponse(responseCode = "201", description = "Игрок создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<PlayerResponse>> registerPlayer(@Valid @RequestBody PlayerRequest request);

    @Operation(summary = "Получить игрока по ID")
    @ApiResponse(responseCode = "200", description = "Игрок найден")
    @ApiResponse(responseCode = "404", description = "Игрок не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{playerId}")
    EntityModel<PlayerResponse> getPlayerById(@PathVariable Long playerId);

}
