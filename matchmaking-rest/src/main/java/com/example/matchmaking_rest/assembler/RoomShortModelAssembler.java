package com.example.matchmaking_rest.assembler;

import com.example.matchmaking_rest.controller.RoomController;
import com.example.matcmaking_api.dto.room.RoomShortResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoomShortModelAssembler implements RepresentationModelAssembler<RoomShortResponse, EntityModel<RoomShortResponse>> {

    @Override
    public EntityModel<RoomShortResponse> toModel(RoomShortResponse room) {
        return EntityModel.of(room,
                linkTo(methodOn(RoomController.class).getRoomById(room.getRoomId())).withSelfRel()
        );
    }
}