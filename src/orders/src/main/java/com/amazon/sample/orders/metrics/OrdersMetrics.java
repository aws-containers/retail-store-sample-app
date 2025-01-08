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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrdersMetrics {

  private Counter orderCreatedCounter;
  private AtomicInteger orderTotal;
  private MeterRegistry meterRegistry;
  private Map<String, Counter> watchCounters = new HashMap<>();

  public OrdersMetrics(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
    this.orderCreatedCounter = Counter.builder("watch.orders")
      .tag("productId", "*")
      .description("The number of orders placed")
      .register(meterRegistry);
    this.orderTotal = new AtomicInteger(0);
    meterRegistry.gauge("watch.orderTotal", orderTotal);
  }

  @TransactionalEventListener
  public void onOrderCreated(OrderCreatedEvent event) {
    this.orderCreatedCounter.increment();
    for (OrderItemEntity orderentity : event.getOrder().getOrderItems()) {
      getCounter(orderentity).increment(orderentity.getQuantity());
    }
    int totalPrice = event
      .getOrder()
      .getOrderItems()
      .stream()
      .map(x -> x.getTotalCost())
      .reduce(0, Integer::sum);
    this.orderTotal.getAndAdd(totalPrice);
  }

  private Counter getCounter(OrderItemEntity orderentity) {
    if (null == watchCounters.get(orderentity.getProductId())) {
      Counter counter = Counter.builder("watch.orders")
        .tag("productId", orderentity.getProductId())
        .register(meterRegistry);
      watchCounters.put(orderentity.getProductId(), counter);
    }
    return watchCounters.get(orderentity.getProductId());
  }
}
