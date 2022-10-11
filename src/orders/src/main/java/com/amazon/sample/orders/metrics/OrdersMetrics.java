package com.amazon.sample.orders.metrics;

import com.amazon.sample.events.orders.OrderCreatedEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrdersMetrics {

    private Counter orderCreatedCounter;

    public OrdersMetrics(MeterRegistry meterRegistry) {
        this.orderCreatedCounter = meterRegistry.counter("orders_created");
    }

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        this.orderCreatedCounter.increment();
    }
}
