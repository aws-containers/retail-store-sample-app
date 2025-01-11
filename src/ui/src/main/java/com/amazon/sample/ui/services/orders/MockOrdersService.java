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

package com.amazon.sample.ui.services.orders;

import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.orders.model.Order;
import com.amazon.sample.ui.services.orders.model.OrderItem;
import com.amazon.sample.ui.services.orders.model.PreparedOrder;
import com.amazon.sample.ui.services.orders.model.ShippingAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

public class MockOrdersService implements OrdersService {

  private int SHIPPING_COST = 10;

  private CartsService cartsService;

  public MockOrdersService(CartsService cartsService) {
    this.cartsService = cartsService;
  }

  @Override
  public Mono<Order> placeOrder(
    String sessionId,
    ShippingAddress shippingAddress
  ) {
    return getItemsWithTotal(sessionId).map(i -> {
      String orderId = UUID.randomUUID().toString();

      int subtotal = i.total;
      int tax = 10;
      int total = subtotal + tax + SHIPPING_COST;

      return new Order(
        orderId,
        subtotal,
        tax,
        SHIPPING_COST,
        total,
        shippingAddress,
        i.items
      );
    });
  }

  // Helper class to hold both items and total
  private static class ItemsWithTotal {

    final List<OrderItem> items;
    final int total;

    ItemsWithTotal(List<OrderItem> items, int total) {
      this.items = items;
      this.total = total;
    }
  }

  @Override
  public Mono<PreparedOrder> prepareOrder(String sessionId) {
    return getItemsWithTotal(sessionId).map(i -> {
      int subtotal = i.total;
      int tax = 10;
      int total = subtotal + tax + SHIPPING_COST;

      return new PreparedOrder(subtotal, tax, SHIPPING_COST, total, i.items);
    });
  }

  private Mono<ItemsWithTotal> getItemsWithTotal(String sessionId) {
    Mono<Cart> cart = cartsService.getCart(sessionId);

    return cart.map(c -> {
      return c
        .getItems()
        .stream()
        .map(i -> {
          var subtotal = i.getPrice() * i.getQuantity();
          return new OrderItem(
            i.getId(),
            i.getName(),
            i.getQuantity(),
            i.getPrice(),
            subtotal
          );
        })
        .collect(
          Collectors.teeing(
            Collectors.toList(), // Collect items to a list
            Collectors.summingInt(item -> item.getTotalCost()), // Sum all subtotals
            (items, total) -> new ItemsWithTotal(items, total) // Combine results
          )
        );
    });
  }
}
