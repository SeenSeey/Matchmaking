package com.example.statistics_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "matchmaking-exchange";
    public static final String QUEUE = "statistics-events-queue";

    @Bean
    public TopicExchange matchmakingExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue statisticsQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "dlx-exchange")
                .withArgument("x-dead-letter-routing-key", "dlq.statistics")
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(statisticsQueue())
                .to(matchmakingExchange())
                .with("room.*");
    }
}
