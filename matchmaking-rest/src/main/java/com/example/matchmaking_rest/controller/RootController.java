package com.example.matchmaking_rest.controller;

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
                linkTo(methodOn(PlayerController.class).getPlayerById(1L)).withRel("player-by-id"),
                linkTo(methodOn(PlayerController.class).registerPlayer(null)).withRel("register-player"),
                linkTo(methodOn(RoomController.class).getRoomById(1L)).withRel("room-by-id"),
                linkTo(methodOn(RoomController.class).searchRooms(null, null, null, 0, 10)).withRel("rooms"),
                linkTo(methodOn(RoomController.class).recommendRooms(0, 10)).withRel("recommended-rooms"),
                linkTo(methodOn(RootController.class).getRoot()).slash("swagger-ui.html").withRel("documentation")
        );

        return rootModel;
    }
}