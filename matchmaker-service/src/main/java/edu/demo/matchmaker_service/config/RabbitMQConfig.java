package edu.demo.matchmaker_service.config;

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
    public static final String MATCH_FOUND_QUEUE = "match.found";
    public static final String PLAYER_LEFT_QUEUE_QUEUE = "player.left.queue";
    public static final String PLAYER_SEARCHING_DLQ = "player.searching.opponent.dlq";
    public static final String MATCH_FOUND_DLQ = "match.found.dlq";
    public static final String PLAYER_LEFT_QUEUE_DLQ = "player.left.queue.dlq";
    public static final String EXCHANGE = "matchmaking.exchange";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue playerSearchingQueue() {
        return QueueBuilder.durable(PLAYER_SEARCHING_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_SEARCHING_DLQ)
                .build();
    }

    @Bean
    public Queue playerSearchingDlq() {
        return QueueBuilder.durable(PLAYER_SEARCHING_DLQ).build();
    }

    @Bean
    public Queue matchFoundQueue() {
        return QueueBuilder.durable(MATCH_FOUND_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", MATCH_FOUND_DLQ)
                .build();
    }

    @Bean
    public Queue matchFoundDlq() {
        return QueueBuilder.durable(MATCH_FOUND_DLQ).build();
    }

    @Bean
    public Queue playerLeftQueueQueue() {
        return QueueBuilder.durable(PLAYER_LEFT_QUEUE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", PLAYER_LEFT_QUEUE_DLQ)
                .build();
    }

    @Bean
    public Queue playerLeftQueueDlq() {
        return QueueBuilder.durable(PLAYER_LEFT_QUEUE_DLQ).build();
    }

    @Bean
    public Binding playerSearchingBinding() {
        return BindingBuilder.bind(playerSearchingQueue())
                .to(exchange())
                .with(PLAYER_SEARCHING_QUEUE);
    }

    @Bean
    public Binding matchFoundBinding() {
        return BindingBuilder.bind(matchFoundQueue())
                .to(exchange())
                .with(MATCH_FOUND_QUEUE);
    }

    @Bean
    public Binding playerLeftQueueBinding() {
        return BindingBuilder.bind(playerLeftQueueQueue())
                .to(exchange())
                .with(PLAYER_LEFT_QUEUE_QUEUE);
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
