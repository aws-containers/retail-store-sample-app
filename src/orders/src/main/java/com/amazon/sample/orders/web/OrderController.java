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

package com.amazon.sample.orders.web;

import com.amazon.sample.orders.services.OrderService;
import com.amazon.sample.orders.web.payload.ExistingOrder;
import com.amazon.sample.orders.web.payload.Order;
import com.amazon.sample.orders.web.payload.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Tag(name = "orders")
@Slf4j
public class OrderController {

  @Autowired
  private OrderService service;

  @Autowired
  private OrderMapper orderMapper;

  @PostMapping(produces = { "application/json" })
  @Operation(summary = "Create an order", operationId = "createOrder")
  public ExistingOrder order(@RequestBody Order orderRequest) {
    log.debug("Creating order {}", orderRequest);

    return this.orderMapper.toExistingOrder(
        this.service.create(this.orderMapper.toOrderEntity(orderRequest))
      );
  }

  @GetMapping(produces = { "application/json" })
  @Operation(summary = "List orders", operationId = "listOrders")
  public List<ExistingOrder> order() {
    return this.service.list()
      .stream()
      .peek(o -> System.out.println("Order: " + o))
      .map(this.orderMapper::toExistingOrder)
      .collect(Collectors.toList());
  }
}
