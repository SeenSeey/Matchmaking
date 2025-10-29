package com.example.audit_service.listener;

import com.example.events_contract.PlayerJoinedRoomEvent;
import com.example.events_contract.PlayerLeftRoomEvent;
import com.example.events_contract.RoomCreatedEvent;
import com.example.events_contract.RoomUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {
    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "audit-room-created-queue", durable = "true"),
            exchange = @Exchange(name = "matchmaking-exchange", type = "topic"),
            key = "room.created"
    ))
    public void handleRoomCreated(RoomCreatedEvent event) {
        log.info("==== ROOM CREATED: id={}, name={}, map={}, maxPlayers={}, status={}",
                event.roomId(), event.name(), event.roomMap(), event.maxPlayers(), event.status());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "audit-room-updated-queue", durable = "true"),
            exchange = @Exchange(name = "matchmaking-exchange", type = "topic"),
            key = "room.updated"
    ))
    public void handleRoomUpdated(RoomUpdatedEvent event) {
        log.info("===== ROOM UPDATED: id={}, name={}, map={}, maxPlayers={}, status={}",
                event.roomId(), event.name(), event.roomMap(), event.maxPlayers(), event.status());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "audit-player-joined-queue", durable = "true"),
            exchange = @Exchange(name = "matchmaking-exchange", type = "topic"),
            key = "room.player.joined"
    ))
    public void handlePlayerJoined(PlayerJoinedRoomEvent event) {
        log.info("==== PLAYER JOINED ROOM: roomId={}, playerId={}, currentPlayers={}",
                event.roomId(), event.playerId(), event.currentPlayersCount());
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "audit-player-left-queue", durable = "true"),
            exchange = @Exchange(name = "matchmaking-exchange", type = "topic"),
            key = "room.player.left"
    ))
    public void handlePlayerLeft(PlayerLeftRoomEvent event) {
        log.info("==== PLAYER LEFT ROOM: roomId={}, playerId={}, currentPlayers={}",
                event.roomId(), event.playerId(), event.currentPlayersCount());
    }
}
