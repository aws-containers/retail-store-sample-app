/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sample.orders.config.messaging;

import com.amazon.sample.orders.messaging.MessagingProvider;
import com.amazon.sample.orders.messaging.rabbitmq.RabbitMQMessagingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
@Slf4j
@EnableConfigurationProperties(RabbitMQProperties.class)
@ConditionalOnProperty(
  prefix = MessagingProperties.PREFIX,
  name = "provider",
  havingValue = "rabbitmq"
)
public class RabbitMQMessagingConfig
  extends RabbitAutoConfiguration
  implements RabbitListenerConfigurer {

  @Autowired
  private RabbitMQProperties properties;

  public static final String EXCHANGE_NAME = "orders-exchange";

  public static final String ORDERS_ORDERS_QUEUE = "orders-orders-queue";

  @Bean
  public MessagingProvider messagingProvider(RabbitTemplate template) {
    log.info("Using RabbitMQ messaging");
    log.info(
      "RabbitMQ addresses: {}",
      String.join(",", this.properties.getAddresses())
    );

    return new RabbitMQMessagingProvider(template);
  }

  @Bean
  Queue queue() {
    return new Queue(ORDERS_ORDERS_QUEUE, false);
  }

  @Bean
  FanoutExchange exchange() {
    return new FanoutExchange(EXCHANGE_NAME);
  }

  @Bean
  Binding binding(Queue queue, FanoutExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange);
  }

  @Bean
  @Primary
  public RabbitTemplate customRabbitTemplate(
    final ConnectionFactory connectionFactory
  ) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // =======

  @Override
  public void configureRabbitListeners(
    RabbitListenerEndpointRegistrar registrar
  ) {
    registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
  }

  @Bean
  MessageHandlerMethodFactory messageHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory messageHandlerMethodFactory =
      new DefaultMessageHandlerMethodFactory();
    messageHandlerMethodFactory.setMessageConverter(
      consumerJackson2MessageConverter()
    );
    return messageHandlerMethodFactory;
  }

  @Bean
  public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
    return new MappingJackson2MessageConverter();
  }
}
