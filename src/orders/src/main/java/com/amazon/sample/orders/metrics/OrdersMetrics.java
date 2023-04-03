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
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrdersMetrics {

    private Counter orderCreatedCounter;
    private MeterRegistry meterRegistry;
    private Gauge orderTotal;
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
                .tag("type","pocketWatch")
                .description("The number of orders placed for Pocket watch")
                .register(meterRegistry);
        this.woodWatchCounter = Counter.builder("watch.orders")
                .tag("type","woodWatch")
                .description("The number of orders placed for Wood watch")
                .register(meterRegistry);
        this.classicWatchCounter = Counter.builder("watch.orders")
                .tag("type","classicWatch")
                .description("The number of orders placed for Classic watch")
                .register(meterRegistry);
        this.gentlemanWatchCounter = Counter.builder("watch.orders")
                .tag("type","gentlemanWatch")
                .description("The number of orders placed for Gentleman watch")
                .register(meterRegistry);
    }

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {

        this.orderCreatedCounter.increment();
        System.out.println("--- num of orders " + event.getOrder().getOrderItems().size());
        for (OrderItemEntity orderentity : event.getOrder().getOrderItems()) {
            System.out.println(" ---- ORDER = " + orderentity.getPrice());
            System.out.println(" ---- ORDER = " + orderentity.getQuantity());
            System.out.println(" ---- ORDER = " + orderentity.getProductId());
            System.out.println(" ---- ORDER = " + orderentity.getTotalCost());
            System.out.println(" ---- ORDER = " + orderentity.getName());
        }
        for (OrderItemEntity orderentity : event.getOrder().getOrderItems()) {
            switch(orderentity.getName()){
                case POCKET_WATCH: this.pocketWatchCounter.increment();break;
                case WOOD_WATCH: this.woodWatchCounter.increment();break;
                case CHRONOGRAF_WATCH: this.classicWatchCounter.increment();break;
                case GENTLEMAN_WATCH: this.gentlemanWatchCounter.increment();break;
            }
        }
        int totalPrice = event.getOrder().getOrderItems().stream().map(x -> x.getTotalCost()).reduce(0, Integer::sum);
        System.out.println("---TOTAL Price  " + totalPrice);
        meterRegistry.gauge("watch.orderTotal", new AtomicInteger(totalPrice));
        System.out.print("___ Added to Gauage ---------");
    }
}
