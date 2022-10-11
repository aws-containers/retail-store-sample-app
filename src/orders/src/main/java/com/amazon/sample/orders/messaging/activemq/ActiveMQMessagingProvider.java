package com.amazon.sample.orders.messaging.activemq;

import com.amazon.sample.events.orders.OrderCreatedEvent;
import com.amazon.sample.orders.messaging.MessagingProvider;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

public class ActiveMQMessagingProvider implements MessagingProvider {

    private final static String ORDERS_TOPIC = "VirtualTopic.orders";

    private JmsTemplate jmsTemplate;

    public ActiveMQMessagingProvider(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void publishEvent(Object event) {
        this.jmsTemplate.convertAndSend(new ActiveMQTopic(ORDERS_TOPIC), event);
    }

    // TODO: Consume our own messages for testing right now
    @JmsListener(destination = "Consumer.orders-orders.VirtualTopic.orders")
    public void receive(OrderCreatedEvent message) {
        System.out.println("Message: " + message);
    }
}
