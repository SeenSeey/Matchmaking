package edu.demo.user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String GAME_FINISHED_QUEUE = "game.finished";
    public static final String PLAYER_STATS_UPDATED_QUEUE = "player.stats.updated";
    public static final String PLAYER_STATUS_UPDATE_QUEUE = "player.status.update";
    public static final String GAME_FINISHED_DLQ = "game.finished.dlq";
    public static final String PLAYER_STATS_UPDATED_DLQ = "player.stats.updated.dlq";
    public static final String PLAYER_STATUS_UPDATE_DLQ = "player.status.update.dlq";
    public static final String EXCHANGE = "matchmaking.exchange";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue gameFinishedQueue() {
        return QueueBuilder.durable(GAME_FINISHED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", GAME_FINISHED_DLQ)
                .build();
    }

    @Bean
    public Queue gameFinishedDlq() {
        return QueueBuilder.durable(GAME_FINISHED_DLQ).build();
    }

    @Bean
    public Queue playerStatsUpdatedQueue() {
        return QueueBuilder.durable(PLAYER_STATS_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_STATS_UPDATED_DLQ)
                .build();
    }

    @Bean
    public Queue playerStatsUpdatedDlq() {
        return QueueBuilder.durable(PLAYER_STATS_UPDATED_DLQ).build();
    }

    @Bean
    public Binding gameFinishedBinding() {
        return BindingBuilder.bind(gameFinishedQueue())
                .to(exchange())
                .with(GAME_FINISHED_QUEUE);
    }

    @Bean
    public Queue playerStatusUpdateQueue() {
        return QueueBuilder.durable(PLAYER_STATUS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_STATUS_UPDATE_DLQ)
                .build();
    }

    @Bean
    public Queue playerStatusUpdateDlq() {
        return QueueBuilder.durable(PLAYER_STATUS_UPDATE_DLQ).build();
    }

    @Bean
    public Binding playerStatsUpdatedBinding() {
        return BindingBuilder.bind(playerStatsUpdatedQueue())
                .to(exchange())
                .with(PLAYER_STATS_UPDATED_QUEUE);
    }

    @Bean
    public Binding playerStatusUpdateBinding() {
        return BindingBuilder.bind(playerStatusUpdateQueue())
                .to(exchange())
                .with(PLAYER_STATUS_UPDATE_QUEUE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                        Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
