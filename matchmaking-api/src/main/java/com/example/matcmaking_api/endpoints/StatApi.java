package com.example.matcmaking_api.endpoints;

import com.example.matcmaking_api.dto.StatusResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatAllTimeResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatLastGameResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "stats", description = "API работы со статистикой")
@RequestMapping("/api/stats")
public interface StatApi {

    @Operation(summary = "Получить статистику игрока за последнюю игру")
    @ApiResponse(responseCode = "200", description = "Статистика получена")
    @ApiResponse(responseCode = "404", description = "Статистика не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/lastGame")
    EntityModel<PlayerStatLastGameResponse> getPlayerStatLastGame(
            @Parameter(description = "ID игрока") @RequestParam @NotNull Long playerId);

    @Operation(summary = "Получить статистику игрока за всё время")
    @ApiResponse(responseCode = "200", description = "Статистика получена")
    @ApiResponse(responseCode = "404", description = "Статистика не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/allTime")
    EntityModel<PlayerStatAllTimeResponse> getPlayerStatAllTime(
            @Parameter(description = "ID игрока") @RequestParam @NotNull Long playerId);

    @Operation(summary = "Получить статистику лучших игроков")
    @ApiResponse(responseCode = "200", description = "Список лучших игроков")
    @GetMapping("/topPlayers")
    PagedModel<EntityModel<PlayerStatAllTimeResponse>> getTopPlayersPages(
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size
    );

}
