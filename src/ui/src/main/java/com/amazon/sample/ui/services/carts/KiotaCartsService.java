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

import com.amazon.sample.ui.client.cart.CartClient;
import com.amazon.sample.ui.client.cart.models.Item;
import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.model.Product;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class KiotaCartsService implements CartsService {

  private CatalogService catalogService;
  private CartClient cartClient;

  public KiotaCartsService(
    CartClient cartClient,
    CatalogService catalogService
  ) {
    this.cartClient = cartClient;
    this.catalogService = catalogService;
  }

  @Override
  public Mono<Cart> getCart(String sessionId) {
    return this.createCart(
        this.cartClient.carts().byCustomerId(sessionId).get()
      );
  }

  @Override
  public Mono<Cart> deleteCart(String sessionId) {
    return this.createCart(
        this.cartClient.carts().byCustomerId(sessionId).delete()
      );
  }

  @Override
  public Mono<Void> addItem(String sessionId, String productId, int quantity) {
    return this.catalogService.getProduct(productId)
      .map(p -> {
        var item = new Item();
        item.setItemId(p.getId());
        item.setQuantity(quantity);
        item.setUnitPrice(p.getPrice());

        return item;
      })
      .flatMap(i ->
        Mono.just(
          this.cartClient.carts().byCustomerId(sessionId).items().post(i)
        )
      )
      .then();
  }

  @Override
  public Mono<Void> removeItem(String sessionId, String productId) {
    this.cartClient.carts()
      .byCustomerId(sessionId)
      .items()
      .byItemId(productId)
      .delete();

    return Mono.empty();
  }

  private Mono<Cart> createCart(
    com.amazon.sample.ui.client.cart.models.Cart cart
  ) {
    return Mono.just(cart)
      .flatMapMany(c -> Flux.fromIterable(c.getItems()))
      .flatMap(i ->
        this.catalogService.getProduct(i.getItemId()).map(p -> {
            return this.toCartItem(i, p);
          })
      )
      .collectList()
      .map(this::toCart);
  }

  private Cart toCart(List<CartItem> items) {
    return new Cart(items);
  }

  private CartItem toCartItem(Item item, Product product) {
    return new CartItem(
      product.getId(),
      item.getQuantity(),
      product.getPrice(),
      product.getName()
    );
  }
}
