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

package com.amazon.sample.carts.services;

import com.amazon.sample.carts.repositories.ItemEntity;
import lombok.extern.slf4j.Slf4j;
import com.amazon.sample.carts.repositories.mongo.MongoCartRepository;
import com.amazon.sample.carts.repositories.mongo.MongoItemRepository;
import com.amazon.sample.carts.repositories.mongo.entities.MongoCartEntity;
import com.amazon.sample.carts.repositories.mongo.entities.MongoItemEntity;

import java.util.List;
import java.util.Optional;

@Slf4j
public class MongoCartService implements CartService {

    private final MongoCartRepository cartRepository;

    private final MongoItemRepository itemRepository;

    public MongoCartService(MongoCartRepository cartRepository, MongoItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    private MongoCartEntity create(String customerId) {
        log.debug("Creating new cart for {}", customerId);

        return cartRepository.save(new MongoCartEntity(customerId));
    }

    @Override
    public MongoCartEntity get(String customerId) {
        return cartRepository.findById(customerId)
                .orElseGet(() -> this.create(customerId));
    }

    @Override
    public void delete(String customerId) {
        log.debug("Deleting cart for {}", customerId);

        this.cartRepository.deleteById(customerId);
    }

    @Override
    public MongoCartEntity merge(String sessionId, String customerId) {
        MongoCartEntity sessionCart = this.get(sessionId);
        MongoCartEntity customerCart = this.get(customerId);

        // TODO: Implement this

        this.cartRepository.deleteById(sessionId);

        return customerCart;
    }

    @Override
    public ItemEntity add(String customerId, String itemId, int quantity, int unitPrice) {
        MongoCartEntity cart = get(customerId);

        MongoItemEntity item = new MongoItemEntity(customerId+itemId, itemId, quantity, unitPrice);

        cart.add(item);

        item = itemRepository.save(item);
        cartRepository.save(cart);

        return item;
    }

    @Override
    public List<MongoItemEntity> items(String customerId) {
        return this.get(customerId).getItems();
    }

    @Override
    public Optional<MongoItemEntity> item(String customerId, String itemId) {
        return this.itemRepository.findById(customerId+itemId);
    }

    @Override
    public void deleteItem(String customerId, String itemId) {
        MongoCartEntity cart = this.get(customerId);

        cart.getItems().removeIf(
            item -> item.getItemId().equals(itemId)
        );

        this.cartRepository.save(cart);

        this.itemRepository.deleteById(customerId+itemId);
    }

    @Override
    public Optional<MongoItemEntity> update(String customerId, String itemId, int quantity, int unitPrice) {
        return item(customerId, itemId).map(
            item -> {
                item.setQuantity(quantity);
                item.setUnitPrice(unitPrice);

                return this.itemRepository.save(item);
            }
        );
    }

    @Override
    public boolean exists(String customerId) {
        return cartRepository.findById(customerId).isPresent();
    }
}
