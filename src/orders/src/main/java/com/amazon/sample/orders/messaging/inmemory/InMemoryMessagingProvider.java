package com.amazon.sample.orders.messaging.inmemory;

import com.amazon.sample.orders.messaging.MessagingProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryMessagingProvider implements MessagingProvider {

    public InMemoryMessagingProvider() {
        log.warn("Using in-memory messaging provider");
    }

    @Override
    public void publishEvent(Object event) {
        log.info("Publishing event {}", event);
    }
}
