package com.example.audit_service.listener;

import com.example.events_contract.PlayerJoinedRoomEvent;
import com.example.events_contract.PlayerLeftRoomEvent;
import com.example.events_contract.RoomCreatedEvent;
import com.example.events_contract.RoomUpdatedEvent;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RabbitListener(
        containerFactory = "rabbitListenerContainerFactory",
        bindings = @QueueBinding(
                value = @Queue(
                        name = "audit-events-queue",
                        durable = "true",
                        arguments = {
                                @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                @Argument(name = "x-dead-letter-routing-key", value = "dlq.audit")
                        }),
                exchange = @Exchange(name = "matchmaking-exchange", type = "topic"),
                key = "room.#"
        )
)
public class EventListener {
    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitHandler
    public void handle(RoomCreatedEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        String key = "RoomCreated:" + event.roomId();
        if (!processedEvents.add(key)) {
            log.warn("Duplicate RoomCreatedEvent received for roomId: {}", event.roomId());
            channel.basicAck(tag, false);
            return;
        }

        log.info("ROOM CREATED {}", event);

        channel.basicAck(tag, false);
    }

    @RabbitHandler
    public void handle(RoomUpdatedEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        String key = "RoomUpdated:" + event.roomId();
        if (!processedEvents.add(key)) {
            log.warn("Duplicate RoomUpdatedEvent received for roomId: {}", event.roomId());
            channel.basicAck(tag, false);
            return;
        }

        log.info("ROOM UPDATED {}", event);
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    public void handle(PlayerJoinedRoomEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        String key = "PlayerJoined:" + event.roomId() + ":" + event.playerId();
        if (!processedEvents.add(key)) {
            log.warn("Duplicate PlayerJoinedRoomEvent received for roomId: {}, playerId: {}",
                    event.roomId(), event.playerId());
            channel.basicAck(tag, false);
            return;
        }

        log.info("PLAYER JOINED {}", event);
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    public void handle(PlayerLeftRoomEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        String key = "PlayerLeft:" + event.roomId() + ":" + event.playerId();
        if (!processedEvents.add(key)) {
            log.warn("Duplicate PlayerLeftRoomEvent received for roomId: {}, playerId: {}",
                    event.roomId(), event.playerId());
            channel.basicAck(tag, false);
            return;
        }

        log.info("PLAYER LEFT {}", event);
        channel.basicAck(tag, false);
    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(Object unknown, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        log.error("Unknown event type: {}", unknown);
        channel.basicNack(tag, false, false);
    }
}
