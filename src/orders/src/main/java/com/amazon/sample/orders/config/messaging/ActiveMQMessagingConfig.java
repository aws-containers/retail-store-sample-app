package com.amazon.sample.orders.config.messaging;

import com.amazon.sample.orders.messaging.MessagingProvider;
import com.amazon.sample.orders.messaging.activemq.ActiveMQMessagingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@Profile("activemq")
@EnableJms
public class ActiveMQMessagingConfig {

    @Bean
    public MessagingProvider messagingProvider(JmsTemplate template) {
        return new ActiveMQMessagingProvider(template);
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
