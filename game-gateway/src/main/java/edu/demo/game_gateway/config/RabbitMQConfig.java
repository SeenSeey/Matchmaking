package edu.demo.game_gateway.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PLAYER_SEARCHING_QUEUE = "player.searching.opponent";
    public static final String PLAYER_DISCONNECTED_QUEUE = "player.disconnected";
    public static final String PLAYER_LEFT_QUEUE_QUEUE = "player.left.queue";
    public static final String PLAYER_STATUS_UPDATE_QUEUE = "player.status.update";
    public static final String PLAYER_STATS_UPDATED_QUEUE = "player.stats.updated";
    public static final String EXCHANGE = "matchmaking.exchange";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue playerSearchingQueue() {
        return QueueBuilder.durable(PLAYER_SEARCHING_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_SEARCHING_QUEUE + ".dlq")
                .build();
    }

    @Bean
    public Queue playerLeftQueueQueue() {
        return QueueBuilder.durable(PLAYER_LEFT_QUEUE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_LEFT_QUEUE_QUEUE + ".dlq")
                .build();
    }

    @Bean
    public Binding playerLeftQueueBinding() {
        return BindingBuilder.bind(playerLeftQueueQueue())
                .to(exchange())
                .with(PLAYER_LEFT_QUEUE_QUEUE);
    }

    @Bean
    public Queue playerStatsUpdatedQueue() {
        return QueueBuilder.durable(PLAYER_STATS_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_STATS_UPDATED_QUEUE + ".dlq")
                .build();
    }

    @Bean
    public Queue playerStatusUpdateQueue() {
        return QueueBuilder.durable(PLAYER_STATUS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_STATUS_UPDATE_QUEUE + ".dlq")
                .build();
    }

    @Bean
    public Queue playerStatusUpdateDlq() {
        return QueueBuilder.durable(PLAYER_STATUS_UPDATE_QUEUE + ".dlq").build();
    }

    @Bean
    public Binding playerStatusUpdateBinding() {
        return BindingBuilder.bind(playerStatusUpdateQueue())
                .to(exchange())
                .with(PLAYER_STATUS_UPDATE_QUEUE);
    }

    @Bean
    public Binding playerStatsUpdatedBinding() {
        return BindingBuilder.bind(playerStatsUpdatedQueue())
                .to(exchange())
                .with(PLAYER_STATS_UPDATED_QUEUE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
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
