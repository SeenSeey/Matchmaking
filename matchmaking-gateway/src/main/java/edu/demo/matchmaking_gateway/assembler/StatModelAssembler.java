package edu.demo.matchmaking_gateway.assembler;

import com.example.matcmaking_api.dto.stat.PlayerStatAllTimeResponse;
import com.example.matcmaking_api.dto.stat.PlayerStatLastGameResponse;
import edu.demo.matchmaking_gateway.controller.StatController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StatModelAssembler implements RepresentationModelAssembler<PlayerStatAllTimeResponse, EntityModel<PlayerStatAllTimeResponse>> {

    public EntityModel<PlayerStatLastGameResponse> toModel(PlayerStatLastGameResponse stat) {
        return EntityModel.of(stat,
                linkTo(methodOn(StatController.class).getPlayerStatLastGame(stat.getPlayer().getPlayerId())).withSelfRel(),
                linkTo(methodOn(StatController.class).getPlayerStatAllTime(stat.getPlayer().getPlayerId())).withRel("allTime"),
                linkTo(methodOn(StatController.class).getTopPlayersPages(0, 20)).withRel("topPlayers")
        );
    }

    @Override
    public EntityModel<PlayerStatAllTimeResponse> toModel(PlayerStatAllTimeResponse stat) {
        return EntityModel.of(stat,
                linkTo(methodOn(StatController.class).getPlayerStatAllTime(stat.getPlayer().getPlayerId())).withSelfRel(),
                linkTo(methodOn(StatController.class).getPlayerStatLastGame(stat.getPlayer().getPlayerId())).withRel("lastGame"),
                linkTo(methodOn(StatController.class).getTopPlayersPages(0, 20)).withRel("topPlayers")
        );
    }

    @Override
    public CollectionModel<EntityModel<PlayerStatAllTimeResponse>> toCollectionModel(Iterable<? extends PlayerStatAllTimeResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(StatController.class).getTopPlayersPages(0, 20)).withSelfRel());
    }
}
