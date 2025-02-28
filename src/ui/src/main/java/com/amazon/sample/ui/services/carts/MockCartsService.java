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

package com.amazon.sample.ui.services.carts;

import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.catalog.CatalogService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

public class MockCartsService implements CartsService {

  private CatalogService catalogService;

  private Map<String, Cart> carts;

  public MockCartsService(CatalogService catalogService) {
    this.catalogService = catalogService;

    this.carts = new HashMap<>();
  }

  @Override
  public Mono<Cart> getCart(String sessionId) {
    return Mono.just(getOrCreate(sessionId));
  }

  private Cart getOrCreate(String sessionId) {
    Cart cart;

    if (!carts.containsKey(sessionId)) {
      cart = this.create(sessionId);
    } else {
      cart = carts.get(sessionId);
    }

    return cart;
  }

  private Cart create(String sessionId) {
    Cart cart = new Cart(new ArrayList<>());

    this.carts.put(sessionId, cart);

    return cart;
  }

  @Override
  public Mono<Cart> deleteCart(String sessionId) {
    return Mono.just(this.create(sessionId));
  }

  @Override
  public Mono<Void> addItem(String sessionId, String productId, int quantity) {
    Cart cart = getOrCreate(sessionId);

    return this.catalogService.getProduct(productId)
      .map(p -> new CartItem(productId, quantity, p.getPrice(), p.getName()))
      .doOnNext(i -> cart.addItem(i))
      .then();
  }

  @Override
  public Mono<Void> removeItem(String sessionId, String productId) {
    getOrCreate(sessionId).removeItem(productId);

    return Mono.empty();
  }
}
