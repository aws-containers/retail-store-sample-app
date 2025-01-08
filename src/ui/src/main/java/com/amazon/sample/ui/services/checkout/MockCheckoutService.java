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

package com.amazon.sample.ui.services.checkout;

import com.amazon.sample.ui.services.carts.CartsService;
import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.checkout.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import reactor.core.publisher.Mono;

public class MockCheckoutService implements CheckoutService {

  private static final int MOCK_TAX = 5;
  private static final int MOCK_SHIPPING_COST = 10;
  private static final int MOCK_SHIPPING_DAYS = 3;

  private final CartsService cartsService;
  private final CheckoutMapper mapper;

  private Map<String, Checkout> checkouts = new HashMap<>();

  public MockCheckoutService(CheckoutMapper mapper, CartsService cartsService) {
    this.mapper = mapper;
    this.cartsService = cartsService;
  }

  @Override
  public Mono<Checkout> get(String sessionId) {
    return Mono.just(checkouts.get(sessionId));
  }

  @Override
  public Mono<Checkout> create(String sessionId) {
    Mono<Cart> cart = cartsService.getCart(sessionId);

    return cart.map(c -> {
      Checkout checkout = new Checkout(
        null,
        "asd",
        c.getSubtotal(),
        -1,
        -1,
        c.getTotalPrice(),
        new ArrayList<>()
      );

      checkout.setItems(
        c
          .getItems()
          .stream()
          .map(mapper::fromCartItem)
          .map(mapper::item)
          .collect(Collectors.toList())
      );

      List<ShippingOption> options = new ArrayList<>();
      options.add(
        new ShippingOption(
          "Standard",
          MOCK_SHIPPING_COST,
          "standard",
          MOCK_SHIPPING_DAYS
        )
      );

      checkout.setShippingOptions(options);

      this.checkouts.put(sessionId, checkout);

      return checkout;
    });
  }

  @Override
  public Mono<Checkout> shipping(
    String sessionId,
    String customerEmail,
    ShippingAddress shippingAddress
  ) {
    return get(sessionId).map(checkout -> {
      checkout.setTax(MOCK_TAX);
      checkout.setTotal(checkout.getTotal() + checkout.getTax());

      return checkout;
    });
  }

  @Override
  public Mono<Checkout> delivery(String sessionId, String token) {
    return get(sessionId).map(checkout -> {
      checkout.setShipping(MOCK_SHIPPING_COST);
      checkout.setTotal(checkout.getTotal() + checkout.getShipping());

      return checkout;
    });
  }

  @Override
  public Mono<CheckoutSubmitted> submit(String sessionId) {
    var checkout = this.checkouts.get(sessionId);

    if (checkout == null) {
      return Mono.error(new RuntimeException("Checkout not found"));
    }

    return this.cartsService.deleteCart(sessionId).map(c ->
        new CheckoutSubmitted(
          "1234",
          "something@example.com",
          checkout.getSubtotal(),
          checkout.getTax(),
          checkout.getShipping(),
          checkout.getTotal(),
          checkout.getItems()
        )
      );
  }
}
