package edu.demo.matchmaking_gateway.controller;

import com.example.matcmaking_api.dto.stat.PlayerStatAllTimeResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatLastGameResponse;
import com.example.matcmaking_api.endpoints.StatApi;
import edu.demo.matchmaking_gateway.assembler.StatModelAssembler;
import edu.demo.matchmaking_gateway.service.StatService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatController implements StatApi {
    private final StatService statService;
    private final StatModelAssembler statModelAssembler;

    public StatController(StatService statService, StatModelAssembler statModelAssembler) {
        this.statService = statService;
        this.statModelAssembler = statModelAssembler;
    }

    @Override
    public EntityModel<PlayerStatLastGameResponse> getPlayerStatLastGame(@NotNull Long playerId) {
        PlayerStatLastGameResponse stat = statService.getPlayerStatLastGame(playerId);
        return statModelAssembler.toModel(stat);
    }

    @Override
    public EntityModel<PlayerStatAllTimeResponse> getPlayerStatAllTime(@NotNull Long playerId) {
        PlayerStatAllTimeResponse stat = statService.getPlayerStatAllTime(playerId);
        return statModelAssembler.toModel(stat);
    }

    @Override
    public PagedModel<EntityModel<PlayerStatAllTimeResponse>> getTopPlayersPages(int page, int size) {
        Page<PlayerStatAllTimeResponse> statsPage = statService.getTopPlayersPages(page, size);
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                statsPage.getSize(),
                statsPage.getNumber(),
                statsPage.getTotalElements(),
                statsPage.getTotalPages()
        );
        return PagedModel.of(
                statsPage.getContent().stream()
                        .map(statModelAssembler::toModel)
                        .toList(),
                pageMetadata
        );
    }
}