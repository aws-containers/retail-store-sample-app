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

package com.amazon.sample.ui.services.checkout.model;

import com.amazon.sample.ui.services.carts.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CheckoutMapper {
    @Mapping(source = "request.items", target = "items")
    @Mapping(source = "request.subtotal", target = "subtotal")
    @Mapping(source = "shippingRates.rates", target = "shippingOptions")
    Checkout checkout(com.amazon.sample.ui.clients.checkout.model.Checkout checkout);

    CheckoutSubmitted submitted(com.amazon.sample.ui.clients.checkout.model.CheckoutSubmitted submitted);

    com.amazon.sample.ui.clients.checkout.model.ShippingAddress clientShippingAddress(ShippingAddress address);

    CheckoutItem item(com.amazon.sample.ui.clients.checkout.model.Item clientItem);

    @Mapping(source = "price", target = "unitCost")
    @Mapping(source = "totalPrice", target = "totalCost")
    com.amazon.sample.ui.clients.checkout.model.Item fromCartItem(CartItem cartItem);
}
