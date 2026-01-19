package edu.demo.matchmaking_gateway.assembler;

import com.example.matcmaking_api.dto.player.PlayerResponse;
import edu.demo.matchmaking_gateway.controller.PlayerController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlayerModelAssembler implements RepresentationModelAssembler<PlayerResponse, EntityModel<PlayerResponse>> {

    @Override
    public EntityModel<PlayerResponse> toModel(PlayerResponse player) {
        return EntityModel.of(player,
                linkTo(methodOn(PlayerController.class).getPlayerById(player.getPlayerId())).withSelfRel(),
                linkTo(methodOn(PlayerController.class).registerPlayer(null)).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<PlayerResponse>> toCollectionModel(Iterable<? extends PlayerResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(PlayerController.class).registerPlayer(null)).withSelfRel());
    }
}
