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

import com.amazon.sample.events.orders.Order;
import com.amazon.sample.events.orders.OrderCreatedEvent;
import com.amazon.sample.orders.entities.OrderItemEntity;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

public class OrdersMetricsTests {

    private MeterRegistry meterRegistry;
    private final String PRODUCT_1 = "Product1";
    private final String PRODUCT_2 = "Product2";

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(meterRegistry);
    }

    @AfterEach
    void tearDown() {
        meterRegistry.clear();
        Metrics.globalRegistry.clear();
    }

    @Test
    void testCreateCounterAndIncrement() {
        OrdersMetrics ordersMetrics = new OrdersMetrics(meterRegistry);

        OrderCreatedEvent event = new OrderCreatedEvent();
        Order order = new Order();
        order.setEmail("test@gmail.com");
        order.setFirstName("John");
        order.setId("id");
        order.setLastName("Doe");

        List<OrderItemEntity> orderItems = new ArrayList<>();
        OrderItemEntity item = new OrderItemEntity();
        item.setName("Pocket Watch");
        item.setQuantity(5);
        item.setUnitCost(100);
        item.setTotalCost(500);
        item.setProductId(PRODUCT_1);
        orderItems.add(item);
        item = new OrderItemEntity();
        item.setName("Wood Watch");
        item.setQuantity(2);
        item.setUnitCost(50);
        item.setTotalCost(100);
        item.setProductId(PRODUCT_2);
        orderItems.add(item);

        order.setOrderItems(orderItems);
        event.setOrder(order);
        ordersMetrics.onOrderCreated(event);

        var counter = meterRegistry.get("watch.orders").tags("productId", "*").counter();
        then(counter).isNotNull();
        then(counter.count()).isEqualTo(1);

        var woodWatchCounter = meterRegistry.get("watch.orders").tags("productId", PRODUCT_2).counter();
        then(woodWatchCounter).isNotNull();
        then(woodWatchCounter.count()).isEqualTo(2);

        var pocketWatchCounter = meterRegistry.get("watch.orders").tags("productId", PRODUCT_1).counter();
        then(pocketWatchCounter).isNotNull();
        then(pocketWatchCounter.count()).isEqualTo(5);

        var watchGauge = meterRegistry.find("watch.orderTotal").gauge();
        then(watchGauge).isNotNull();
        then(watchGauge.value()).isEqualTo(600.0);

        ordersMetrics.onOrderCreated(event);

        then(watchGauge.value()).isEqualTo(1200.0);
    }
}
