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

package com.amazon.sample.orders.services;

import com.amazon.sample.orders.entities.OrderEntity;
import com.amazon.sample.orders.messaging.OrdersEventHandler;
import com.amazon.sample.orders.repositories.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderService extends AbstractRelationalEventListener<OrderEntity> {

  @Autowired
  private OrderRepository repository;

  @Autowired
  private OrdersEventHandler eventHandler;

  @Transactional
  public OrderEntity create(OrderEntity order) {
    System.out.println(order);

    OrderEntity entity = repository.save(order);

    return entity;
  }

  public List<OrderEntity> list() {
    return StreamSupport.stream(
      this.repository.findAll().spliterator(),
      false
    ).collect(Collectors.toList());
  }

  protected void onAfterSave(AfterSaveEvent<OrderEntity> orderCreated) {
    this.eventHandler.postCreatedEvent(orderCreated.getEntity());
  }
}
