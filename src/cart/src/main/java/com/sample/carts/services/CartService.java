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

import com.amazon.sample.carts.repositories.CartEntity;
import com.amazon.sample.carts.repositories.ItemEntity;

import java.util.List;
import java.util.Optional;

public interface CartService {
    CartEntity get(String customerId);

    void delete(String customerId);

    CartEntity merge(String sessionId, String customerId);

    ItemEntity add(String customerId, String itemId, int quantity, int unitPrice);

    List<? extends ItemEntity> items(String customerId);

    Optional<? extends ItemEntity> item(String customerId, String itemId);

    void deleteItem(String customerId, String itemId);

    Optional<? extends ItemEntity> update(String customerId, String itemId, int quantity, int unitPrice);

    boolean exists(String customerId);
}
