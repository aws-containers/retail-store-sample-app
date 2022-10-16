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

import com.amazon.sample.ui.clients.carts.api.CartsApi;
import com.amazon.sample.ui.clients.carts.api.ItemsApi;
import com.amazon.sample.ui.clients.carts.model.Item;
import com.amazon.sample.ui.services.carts.model.Cart;
import com.amazon.sample.ui.services.carts.model.CartItem;
import com.amazon.sample.ui.services.catalog.CatalogService;
import com.amazon.sample.ui.services.catalog.model.Product;
import com.amazon.sample.ui.util.RetryUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class WebClientCartsService implements CartsService {

    private CartsApi cartsApi;

    private ItemsApi itemsApi;

    private CatalogService catalogService;

    public WebClientCartsService(CartsApi cartsApi, ItemsApi itemsApi, CatalogService catalogService) {
        this.cartsApi = cartsApi;
        this.itemsApi = itemsApi;
        this.catalogService = catalogService;
    }

    @Override
    public Mono<Cart> getCart(String sessionId) {
        return this.createCart(cartsApi.getCart(sessionId)
                .retryWhen(RetryUtils.apiClientRetrySpec("get cart")));
    }

    @Override
    public Mono<Cart> deleteCart(String sessionId) {
        return this.createCart(this.cartsApi.deleteCart(sessionId));
    }

    @Override
    public Mono<Void> addItem(String sessionId, String productId) {
        return this.catalogService.getProduct(productId)
                .map(p -> new Item().itemId(p.getId()).quantity(1).unitPrice(p.getPrice()))
                .flatMap(i -> this.itemsApi.addItem(sessionId, i)).then();
    }

    @Override
    public Mono<Void> removeItem(String sessionId, String productId) {
        return this.itemsApi.deleteItem(sessionId, productId);
    }

    private Mono<Cart> createCart(Mono<com.amazon.sample.ui.clients.carts.model.Cart> cart) {
        return cart.flatMapMany(c -> Flux.fromIterable(c.getItems()))
                .flatMap(i -> this.catalogService.getProduct(i.getItemId())
                        .map(p -> { return this.toCartItem(i, p); }))
                .collectList()
                .map(this::toCart);
    }

    private Cart toCart(List<CartItem> items) {
        return new Cart(items);
    }

    private CartItem toCartItem(Item item, Product product) {
        return new CartItem(product.getId(),
                item.getQuantity(), product.getPrice(),
                product.getName(), product.getImageUrl());
    }
}
