package com.example.matchmaking_rest.assembler;

import com.example.matchmaking_rest.controller.RoomController;
import com.example.matcmaking_api.dto.room.RoomResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoomModelAssembler implements RepresentationModelAssembler<RoomResponse, EntityModel<RoomResponse>> {

    @Override
    public EntityModel<RoomResponse> toModel(RoomResponse room) {
        return EntityModel.of(room,
                linkTo(methodOn(RoomController.class).getRoomById(room.getRoomId())).withSelfRel(),
                linkTo(methodOn(RoomController.class).searchRooms(null, null, null, 0, 10)).withRel("collection")
        );
    }

    @Override
    public CollectionModel<EntityModel<RoomResponse>> toCollectionModel(Iterable<? extends RoomResponse> entities) {
        List<EntityModel<RoomResponse>> content = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .toList();

        return CollectionModel.of(content,
                linkTo(methodOn(RoomController.class).searchRooms(null, null, null, 0, 10)).withSelfRel());
    }
}
