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
import com.amazon.sample.ui.services.checkout.model.Checkout;
import com.amazon.sample.ui.services.checkout.model.CheckoutItem;
import com.amazon.sample.ui.services.checkout.model.CheckoutMapper;
import com.amazon.sample.ui.services.checkout.model.CheckoutSubmitted;
import com.amazon.sample.ui.services.checkout.model.ShippingAddress;
import com.amazon.sample.ui.services.checkout.model.ShippingOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import reactor.core.publisher.Mono;

public class MockCheckoutService implements CheckoutService {

  private static final int MOCK_TAX = 5;
  private static final int MOCK_SHIPPING_COST = 10;
  private static final int MOCK_SHIPPING_DAYS = 3;

  private final CartsService cartsService;
  private final CheckoutMapper mapper;

  private Map<String, CheckoutData> checkoutDataMap = new HashMap<>();

  public MockCheckoutService(CheckoutMapper mapper, CartsService cartsService) {
    this.mapper = mapper;
    this.cartsService = cartsService;
  }

  @Override
  public Mono<Checkout> get(String sessionId) {
    var checkoutData = checkoutDataMap.get(sessionId);
    return buildCheckout(checkoutData, sessionId);
  }

  @Override
  public Mono<Checkout> create(String sessionId) {
    CheckoutData checkoutData = new CheckoutData();

    this.checkoutDataMap.put(sessionId, checkoutData);

    return buildCheckout(checkoutData, sessionId);
  }

  @Override
  public Mono<Checkout> shipping(
    String sessionId,
    ShippingAddress shippingAddress
  ) {
    var checkoutData = checkoutDataMap.get(sessionId);
    checkoutData.shippingAddress = shippingAddress;

    return buildCheckout(checkoutData, sessionId);
  }

  @Override
  public Mono<Checkout> delivery(String sessionId, String token) {
    var checkoutData = checkoutDataMap.get(sessionId);
    checkoutData.token = token;

    return buildCheckout(checkoutData, sessionId);
  }

  @Override
  public Mono<CheckoutSubmitted> submit(String sessionId) {
    var checkoutData = checkoutDataMap.get(sessionId);

    return this.buildCheckout(checkoutData, sessionId)
      .zipWith(this.cartsService.deleteCart(sessionId))
      .map(tuple -> {
        Checkout checkout = tuple.getT1();
        return new CheckoutSubmitted(
          UUID.randomUUID().toString(),
          checkoutData.shippingAddress.getEmail(),
          checkout.getSubtotal(),
          checkout.getTax(),
          checkout.getShipping(),
          checkout.getTotal(),
          checkout.getItems()
        );
      });
  }

  private Mono<Checkout> buildCheckout(
    CheckoutData checkoutData,
    String sessionId
  ) {
    Mono<Cart> cart = cartsService.getCart(sessionId);

    return cart.map(c -> {
      var checkoutItems = c
        .getItems()
        .stream()
        .map(mapper::cartItem)
        .map(i -> {
          i.setTotalCost(i.getQuantity() * i.getPrice());
          return i;
        })
        .collect(
          Collectors.teeing(
            Collectors.toList(),
            Collectors.summingInt(item -> item.getTotalCost()),
            (items, total) -> new ItemsWithTotal(items, total)
          )
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

      var tax = 0;
      var shipping = 0;

      if (checkoutData.shippingAddress != null) {
        tax = MOCK_TAX;
      }

      if (checkoutData.token != null) {
        shipping = MOCK_SHIPPING_COST;
      }

      var total = checkoutItems.total + tax + shipping;

      return new Checkout(
        checkoutItems.items,
        checkoutItems.total,
        tax,
        shipping,
        total,
        options
      );
    });
  }

  @Data
  private static class ItemsWithTotal {

    private final List<CheckoutItem> items;
    private final int total;

    ItemsWithTotal(List<CheckoutItem> items, int total) {
      this.items = items;
      this.total = total;
    }
  }

  private static class CheckoutData {

    private String token;

    private ShippingAddress shippingAddress;
  }
}
