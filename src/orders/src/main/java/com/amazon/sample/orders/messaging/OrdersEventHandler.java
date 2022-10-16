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

package com.amazon.sample.orders.messaging;

import com.amazon.sample.events.orders.Order;
import com.amazon.sample.events.orders.OrderCreatedEvent;
import com.amazon.sample.orders.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrdersEventHandler {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessagingProvider messagingProvider;

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        this.messagingProvider.publishEvent(event);
    }

    public void postCreatedEvent(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setFirstName(entity.getFirstName());
        order.setLastName(entity.getLastName());
        order.setEmail(entity.getEmail());

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrder(order);

        publisher.publishEvent(event);
    }
}
