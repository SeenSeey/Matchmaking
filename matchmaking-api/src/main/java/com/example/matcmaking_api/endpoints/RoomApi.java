package com.example.matcmaking_api.endpoints;

import com.example.matcmaking_api.dto.StatusResponse;
import com.example.matcmaking_api.dto.room.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "games", description = "API взаимодействия с комнатами")
@RequestMapping("/api/rooms")
public interface RoomApi {

    @Operation(summary = "Создать комнату")
    @ApiResponse(responseCode = "201", description = "Комната успешно создана")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<RoomResponse>> createRoom(@Valid @RequestBody CreateRoomRequest request);

    @Operation(summary = "Получить комнату по ID")
    @ApiResponse(responseCode = "200", description = "Комната найдена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<RoomResponse> getRoomById(@PathVariable Long id);

    @Operation(summary = "Обновить настройки комнаты (владелец или админ)")
    @ApiResponse(responseCode = "200", description = "Комната успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Комната не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<RoomResponse> updateRoom(@PathVariable Long id, @Valid @RequestBody UpdateRoomRequest request);

    @Operation(summary = "Присоединиться к комнате")
    @ApiResponse(responseCode = "200", description = "Успешно присоединился к комнате")
    @ApiResponse(responseCode = "404", description = "Комната не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping("/{id}/join")
    EntityModel<JoinRoomResponse> joinRoom(@PathVariable Long id, @Valid @RequestBody JoinRoomRequest request);

    @Operation(summary = "Покинуть комнату")
    @ApiResponse(responseCode = "204", description = "Покинул комнату")
    @ApiResponse(responseCode = "404", description = "Комната или пользователь не найдены")
    @PostMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void leaveRoom(@PathVariable Long id, @Valid @RequestBody LeaveRoomRequest request);

    @Operation(summary = "Найти подходящие игры (matchmaking) — фильтры + пагинация")
    @ApiResponse(responseCode = "200", description = "Список подходящих комнат")
    @GetMapping("/search")
    PagedModel<EntityModel<RoomShortResponse>> searchRooms(
            @Parameter(description = "Фильтр по количеству игроков") @RequestParam(required = false) Integer maxPlayers,
            @Parameter(description = "Фильтр по картам") @RequestParam(required = false) String roomMap,
            @Parameter(description = "Искать только открытые комнаты") @RequestParam(required = false) Boolean openOnly,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "Запросить персональные рекомендации / подбор матчей (async-friendly, но возвращает snapshot)")
    @ApiResponse(responseCode = "200", description = "Рекомендации комнат")
    @PostMapping("/recommend")
    PagedModel<EntityModel<RoomShortResponse>> recommendRooms(
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size);
}
