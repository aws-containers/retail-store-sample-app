package com.amazon.sample.orders.messaging;

public interface MessagingProvider {
    void publishEvent(Object event);
}
