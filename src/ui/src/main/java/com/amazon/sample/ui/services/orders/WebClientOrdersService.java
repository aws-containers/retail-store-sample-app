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

import com.amazon.sample.ui.clients.orders.api.OrdersApi;
import com.amazon.sample.ui.clients.orders.model.OrderItem;
import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.orders.model.Order;
import reactor.core.publisher.Mono;

public class WebClientOrdersService implements OrdersService {

    private OrdersApi ordersApi;

    private CartsService cartsService;

    public WebClientOrdersService(OrdersApi ordersApi, CartsService cartsService) {
        this.ordersApi = ordersApi;
        this.cartsService = cartsService;
    }

    @Override
    public Mono<Order> order(String sessionId, String firstName, String lastName, String email) {
        com.amazon.sample.ui.clients.orders.model.Order createOrderRequest = new com.amazon.sample.ui.clients.orders.model.Order();
        createOrderRequest.setFirstName(firstName);
        createOrderRequest.setLastName(lastName);
        createOrderRequest.setEmail(email);

        return this.cartsService.getCart(sessionId).flatMap(
                cart -> {
                    for(CartItem item : cart.getItems()) {
                        OrderItem orderItem = new OrderItem()
                                .productId(item.getId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice());

                        createOrderRequest.addItemsItem(orderItem);
                    }

                    return this.ordersApi.createOrder(createOrderRequest);
                })
                .map(o -> new Order(o.getId(), o.getEmail()));
    }
}
