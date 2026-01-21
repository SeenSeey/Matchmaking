package edu.demo.audit_service.config;

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
    public static final String GAME_FINISHED_QUEUE = "game.finished";
    public static final String PLAYER_DISCONNECTED_QUEUE = "player.disconnected";
    public static final String PLAYER_STATS_UPDATED_QUEUE = "player.stats.updated";
    public static final String PLAYER_STATUS_UPDATE_QUEUE = "player.status.update";
    public static final String EXCHANGE = "matchmaking.exchange";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue auditPlayerSearchingQueue() {
        return QueueBuilder.durable("audit." + PLAYER_SEARCHING_QUEUE).build();
    }

    @Bean
    public Queue auditMatchFoundQueue() {
        return QueueBuilder.durable("audit." + MATCH_FOUND_QUEUE).build();
    }

    @Bean
    public Queue auditPlayerLeftQueueQueue() {
        return QueueBuilder.durable("audit." + PLAYER_LEFT_QUEUE_QUEUE).build();
    }

    @Bean
    public Queue auditGameFinishedQueue() {
        return QueueBuilder.durable("audit." + GAME_FINISHED_QUEUE).build();
    }

    @Bean
    public Queue auditPlayerDisconnectedQueue() {
        return QueueBuilder.durable("audit." + PLAYER_DISCONNECTED_QUEUE).build();
    }

    @Bean
    public Queue auditPlayerStatsUpdatedQueue() {
        return QueueBuilder.durable("audit." + PLAYER_STATS_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue auditPlayerStatusUpdateQueue() {
        return QueueBuilder.durable("audit." + PLAYER_STATUS_UPDATE_QUEUE).build();
    }

    @Bean
    public Binding auditPlayerSearchingBinding() {
        return BindingBuilder.bind(auditPlayerSearchingQueue())
                .to(exchange())
                .with(PLAYER_SEARCHING_QUEUE);
    }

    @Bean
    public Binding auditMatchFoundBinding() {
        return BindingBuilder.bind(auditMatchFoundQueue())
                .to(exchange())
                .with(MATCH_FOUND_QUEUE);
    }

    @Bean
    public Binding auditPlayerLeftQueueBinding() {
        return BindingBuilder.bind(auditPlayerLeftQueueQueue())
                .to(exchange())
                .with(PLAYER_LEFT_QUEUE_QUEUE);
    }

    @Bean
    public Binding auditGameFinishedBinding() {
        return BindingBuilder.bind(auditGameFinishedQueue())
                .to(exchange())
                .with(GAME_FINISHED_QUEUE);
    }

    @Bean
    public Binding auditPlayerDisconnectedBinding() {
        return BindingBuilder.bind(auditPlayerDisconnectedQueue())
                .to(exchange())
                .with(PLAYER_DISCONNECTED_QUEUE);
    }

    @Bean
    public Binding auditPlayerStatsUpdatedBinding() {
        return BindingBuilder.bind(auditPlayerStatsUpdatedQueue())
                .to(exchange())
                .with(PLAYER_STATS_UPDATED_QUEUE);
    }

    @Bean
    public Binding auditPlayerStatusUpdateBinding() {
        return BindingBuilder.bind(auditPlayerStatusUpdateQueue())
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
