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

package com.amazon.sample.orders.metrics;

import com.amazon.sample.events.orders.OrderCreatedEvent;
import com.amazon.sample.orders.entities.OrderItemEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrdersMetrics {

    private Counter orderCreatedCounter;
    private MeterRegistry meterRegistry;
    private Counter pocketWatchCounter;
    private Counter woodWatchCounter;
    private Counter gentlemanWatchCounter;
    private Counter classicWatchCounter;

    public static final String POCKET_WATCH = "Pocket Watch";
    public static final String GENTLEMAN_WATCH = "Gentleman";
    public static final String CHRONOGRAF_WATCH = "Chronograf Classic";
    public static final String WOOD_WATCH = "Wood Watch";

    public OrdersMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.orderCreatedCounter = meterRegistry.counter("orders_created");
        this.pocketWatchCounter = Counter.builder("watch.orders")
                .tag("type",POCKET_WATCH)
                .description("The number of orders placed for Pocket watch")
                .register(meterRegistry);
        this.woodWatchCounter = Counter.builder("watch.orders")
                .tag("type",WOOD_WATCH)
                .description("The number of orders placed for Wood watch")
                .register(meterRegistry);
        this.classicWatchCounter = Counter.builder("watch.orders")
                .tag("type",CHRONOGRAF_WATCH)
                .description("The number of orders placed for Classic watch")
                .register(meterRegistry);
        this.gentlemanWatchCounter = Counter.builder("watch.orders")
                .tag("type",GENTLEMAN_WATCH)
                .description("The number of orders placed for Gentleman watch")
                .register(meterRegistry);
    }

    private void incrementCounter(Counter counter, int times){
        for(int i=0;i<times;i++){
            counter.increment();
        }
    }

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {

        this.orderCreatedCounter.increment();
        for (OrderItemEntity orderentity : event.getOrder().getOrderItems()) {
            switch(orderentity.getName()){
                case POCKET_WATCH: incrementCounter(this.pocketWatchCounter,orderentity.getQuantity());break;
                case WOOD_WATCH: incrementCounter(this.woodWatchCounter,orderentity.getQuantity());break;
                case CHRONOGRAF_WATCH: incrementCounter(this.classicWatchCounter,orderentity.getQuantity());break;
                case GENTLEMAN_WATCH: incrementCounter(this.gentlemanWatchCounter,orderentity.getQuantity());break;
            }
        }
        int totalPrice = event.getOrder().getOrderItems().stream().map(x -> x.getTotalCost()).reduce(0, Integer::sum);
        meterRegistry.gauge("watch.orderTotal", new AtomicInteger(totalPrice));

    }
}
