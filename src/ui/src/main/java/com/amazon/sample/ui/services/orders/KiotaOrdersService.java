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

import com.amazon.sample.ui.kiota.*;
import com.amazon.sample.ui.kiota.models.OrderItem;
import com.amazon.sample.ui.kiota.models.ShippingAddress;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.orders.model.Order;
import java.util.ArrayList;
import reactor.core.publisher.Mono;

public class KiotaOrdersService implements OrdersService {

  private OrdersClient client;

  private CartsService cartsService;

  public KiotaOrdersService(OrdersClient client, CartsService cartsService) {
    this.client = client;
    this.cartsService = cartsService;
  }

  @Override
  public Mono<Order> order(
    String sessionId,
    String firstName,
    String lastName,
    String email
  ) {
    var orderItems = new ArrayList<OrderItem>();
    var shippingAddressRequest = new ShippingAddress();
    var createOrderRequest = new com.amazon.sample.ui.kiota.models.Order();

    createOrderRequest.setShippingAddress(shippingAddressRequest);
    return this.cartsService.getCart(sessionId)
      .flatMap(cart -> {
        for (CartItem item : cart.getItems()) {
          OrderItem orderItem = new OrderItem();
          orderItem.setProductId(item.getId());
          orderItem.setQuantity(item.getQuantity());
          orderItem.setUnitCost(item.getPrice());

          orderItems.add(orderItem);
        }

        return Mono.just(this.client.orders().post(createOrderRequest));
      })
      .map(o -> new Order(o.getId()));
  }
}
