package edu.demo.matchmaking_gateway.assembler;

import com.example.matcmaking_api.dto.game.StartGameResponse;
import edu.demo.matchmaking_gateway.controller.GameController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StartGameModelAssembler implements RepresentationModelAssembler<StartGameResponse, EntityModel<StartGameResponse>> {

    @Override
    public EntityModel<StartGameResponse> toModel(StartGameResponse startGame) {
        return EntityModel.of(startGame,
                linkTo(methodOn(GameController.class).startGame(null)).withSelfRel()
        );
    }
}
