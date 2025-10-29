package com.example.matchmaking_rest.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "matchmaking-exchange";

    public static final String ROUTING_KEY_ROOM_CREATED = "room.created";
    public static final String ROUTING_KEY_ROOM_UPDATED = "room.updated";
    public static final String ROUTING_KEY_PLAYER_JOINED = "room.player.joined";
    public static final String ROUTING_KEY_PLAYER_LEFT = "room.player.left";

    @Bean
    public TopicExchange booksExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
}
