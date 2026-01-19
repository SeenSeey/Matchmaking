package edu.demo.matchmaking_gateway.assembler;

import com.example.matcmaking_api.dto.game.GameResponse;
import edu.demo.matchmaking_gateway.controller.GameController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GameModelAssembler implements RepresentationModelAssembler<GameResponse, EntityModel<GameResponse>> {

    @Override
    public EntityModel<GameResponse> toModel(GameResponse game) {
        return EntityModel.of(game,
                linkTo(methodOn(GameController.class).startGame(null)).withSelfRel(),
                linkTo(methodOn(GameController.class).startGame(null)).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<GameResponse>> toCollectionModel(Iterable<? extends GameResponse> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities)
                .add(linkTo(methodOn(GameController.class).startGame(null)).withSelfRel());
    }
}
