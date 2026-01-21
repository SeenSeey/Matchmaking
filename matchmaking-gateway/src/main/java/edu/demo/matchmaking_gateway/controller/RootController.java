package edu.demo.matchmaking_gateway.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();

        rootModel.add(
                linkTo(methodOn(PlayerController.class).getPlayerById("example-uuid")).withRel("player-by-id"),
                linkTo(methodOn(PlayerController.class).registerPlayer(null)).withRel("register-player"),
                linkTo(methodOn(GameController.class).startGame(null)).withRel("start-game"),
                linkTo(methodOn(RootController.class).getRoot()).slash("swagger-ui.html").withRel("documentation")
        );

        return rootModel;
    }
}