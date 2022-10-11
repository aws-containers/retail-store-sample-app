package com.amazon.sample.orders.messaging.rabbitmq;

import com.amazon.sample.orders.config.messaging.RabbitMQMessagingConfig;
import com.amazon.sample.orders.messaging.MessagingProvider;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQMessagingProvider implements MessagingProvider {

    private RabbitTemplate rabbitTemplate;

    public RabbitMQMessagingProvider(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishEvent(Object event) {
        this.rabbitTemplate.convertAndSend(RabbitMQMessagingConfig.EXCHANGE_NAME, "", event);
    }
}
