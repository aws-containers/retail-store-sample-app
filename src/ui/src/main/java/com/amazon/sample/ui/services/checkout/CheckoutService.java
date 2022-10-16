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

import com.amazon.sample.ui.services.checkout.model.Checkout;
import com.amazon.sample.ui.services.checkout.model.CheckoutSubmitted;
import com.amazon.sample.ui.services.checkout.model.CheckoutSubmittedResponse;
import com.amazon.sample.ui.services.checkout.model.ShippingAddress;
import reactor.core.publisher.Mono;

public interface CheckoutService {
    Mono<Checkout> get(String sessionId);

    Mono<Checkout> create(String sessionId);

    Mono<Checkout> shipping(String sessionId, String customerEmail, ShippingAddress shippingAddress);

    Mono<Checkout> delivery(String sessionId, String token);

    Mono<CheckoutSubmittedResponse> submit(String sessionId);
}
