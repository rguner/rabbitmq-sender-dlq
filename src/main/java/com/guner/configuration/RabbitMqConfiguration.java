package com.guner.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Value("${single-sender.topic-exchange.name}")
    private String topicExchange;

    @Value("${single-sender.queue.name.single-queue}")
    private String queueSingle;

    @Value("${single-sender.routing.key.single-routing}")
    private String routingKeySingle;

    @Value("${single-sender.topic-exchange.name}-DLX")
    private String deadLetterExchange;

    // to campatible with consumer-dlq, added deadletterExchange definition
    @Bean
    public Queue queueSingle() {
        return QueueBuilder.durable(queueSingle)
                .deadLetterExchange(deadLetterExchange) // to dead letter exchange
                .deadLetterRoutingKey("deadLetterRoutingKey")
                .build();
    }


    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(topicExchange);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchange);
    }


    @Bean
    public Binding bindingSingle() {
        return BindingBuilder
                .bind(queueSingle())
                .to(topicExchange())
                .with(routingKeySingle);
    }

    /*
    mandatory for message conversion.
    if not defined, below error occurs
    "message": "SimpleMessageConverter only supports String, byte[] and Serializable payloads, received: com.guner.model.ChargingRecord",
     */
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    // sadece MessageConverter bean tanımı da yeterli, rabbitTemplate kullanmak için amqpTemplate tanımı zorunlu değil
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
