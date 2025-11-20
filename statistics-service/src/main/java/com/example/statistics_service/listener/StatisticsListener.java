package com.example.statistics_service.listener;

import com.example.events_contract.RoomCreatedEvent;
import com.example.events_contract.RoomUpdatedEvent;
import com.example.statistics_service.service.StatisticsService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(
        containerFactory = "rabbitListenerContainerFactory",
        bindings = @QueueBinding(
                value = @Queue(
                        name = "statistics-events-queue",
                        durable = "true",
                        arguments = {
                                @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                @Argument(name = "x-dead-letter-routing-key", value = "dlq.statistics")
                        }),
                exchange = @Exchange(name = "matchmaking-exchange", type = "topic"),
                key = "room.#"
        )
)
public class StatisticsListener {

    private static final Logger log = LoggerFactory.getLogger(StatisticsListener.class);

    private final StatisticsService service;

    public StatisticsListener(StatisticsService service) {
        this.service = service;
    }

    @RabbitHandler
    public void handle(RoomCreatedEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        service.update(event);
        channel.basicAck(tag, false);
    }

    @RabbitHandler
    public void handle(RoomUpdatedEvent event, Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        service.update(event);
        channel.basicAck(tag, false);
    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(Object unknown, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        log.warn("Unknown event type received, skipping: {}", unknown);
        channel.basicAck(tag, false);
    }
}
